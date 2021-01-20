/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.dropwizard.worker;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.events.EventAware;
import org.sonatype.goodies.dropwizard.events.EventService;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;
import org.sonatype.goodies.dropwizard.service.ManagedHelper;
import org.sonatype.goodies.dropwizard.common.text.Plural;
import org.sonatype.goodies.dropwizard.worker.internal.SnsEventProducerConfiguration;
import org.sonatype.goodies.dropwizard.worker.internal.SqsEventConsumerConfiguration;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Key;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.eclipse.sisu.inject.BeanLocator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Default {@link WorkerService}.
 *
 * @since ???
 */
@Named
@Singleton
public class WorkerServiceImpl
    extends ServiceSupport
    implements WorkerService, EventAware.Asynchronous
{
  private final BeanLocator beanLocator;

  private final EventService eventService;

  private final WorkEventEnvelopeProducer.Factory producerFactory;

  private final WorkEventEnvelopeConsumer.Factory consumerFactory;

  private final WorkerServiceConfiguration configuration;

  private final Set<WorkEventHandler> handlers = new CopyOnWriteArraySet<>();

  @Nullable
  private WorkEventEnvelopeProducer producer;

  @Nullable
  private WorkEventEnvelopeConsumer consumer;

  @Inject
  public WorkerServiceImpl(final BeanLocator beanLocator,
                           final EventService eventService,
                           final WorkEventEnvelopeProducer.Factory producerFactory,
                           final WorkEventEnvelopeConsumer.Factory consumerFactory,
                           final WorkerServiceConfiguration configuration)
  {
    this.beanLocator = checkNotNull(beanLocator);
    this.eventService = checkNotNull(eventService);
    this.producerFactory = checkNotNull(producerFactory);
    this.consumerFactory = checkNotNull(consumerFactory);
    this.configuration = checkNotNull(configuration);
  }

  @Override
  protected void doStart() throws Exception {
    SnsEventProducerConfiguration producerConfiguration = configuration.getProducerConfiguration();
    if (producerConfiguration != null) {
      producer = producerFactory.create(producerConfiguration);
      log.debug("Producer: {}", producer);
    }

    SqsEventConsumerConfiguration consumerConfiguration = configuration.getConsumerConfiguration();
    if (consumerConfiguration != null) {
      consumer = consumerFactory.create(consumerConfiguration, this::consume);
      log.debug("Consumer: {}", consumer);
    }

    ManagedHelper.start(consumer);
    ManagedHelper.start(producer);

    eventService.register(this);
  }

  /**
   * Mediator to register and unregister {@link WorkEventHandler} components.
   */
  private static class WorkEventHandlerMediator
      implements Mediator<Named, WorkEventHandler, WorkerServiceImpl>
  {
    @Override
    public void add(final BeanEntry<Named, WorkEventHandler> entry, final WorkerServiceImpl watcher) {
      watcher.addHandler(entry.getValue());
    }

    @Override
    public void remove(final BeanEntry<Named, WorkEventHandler> entry, final WorkerServiceImpl watcher) {
      watcher.removeHandler(entry.getValue());
    }
  }

  @Override
  protected void doStarted() throws Exception {
    beanLocator.watch(Key.get(WorkEventHandler.class, Named.class), new WorkEventHandlerMediator(), this);
  }

  @Override
  protected void doStop() throws Exception {
    eventService.unregister(this);

    ManagedHelper.stop(producer);
    producer = null;
    ManagedHelper.stop(consumer);
    consumer = null;
  }

  @Override
  public void addHandler(final WorkEventHandler handler) {
    checkNotNull(handler);
    ensureStarted();
    log.debug("Add handler: {}", handler);

    handlers.add(handler);
  }

  @Override
  public void removeHandler(final WorkEventHandler handler) {
    checkNotNull(handler);
    ensureStarted();
    log.debug("Remove handler: {}", handler);

    handlers.remove(handler);
  }

  /**
   * Adapt {@link DispatchWorkEvent} to {@link #dispatch(WorkEvent)}.
   */
  @Subscribe
  @AllowConcurrentEvents
  public void on(final DispatchWorkEvent event) {
    checkNotNull(event);
    ensureStarted();
    log.debug("ON: {}", event);

    dispatch(event.getEvent());
  }

  @Override
  public void dispatch(final WorkEvent event) {
    checkNotNull(event);
    ensureStarted();
    log.debug("Dispatch: {}", event);

    // if a producer is configured, remote event to it
    if (producer != null) {
      producer.post(new WorkEventEnvelope(event));
    }
    else {
      // else post event locally for asynchronous processing
      eventService.post(new LocalConsumeWorkEvent(event));
    }
  }

  @Subscribe
  @AllowConcurrentEvents
  public void on(final LocalConsumeWorkEvent event) {
    checkNotNull(event);
    ensureStarted();
    log.debug("ON: {}", event);

    consume(event.getEvent());
  }

  /**
   * Consume event from remoted.
   */
  private void consume(final WorkEventEnvelope envelope) {
    log.debug("Consume: {}", envelope);

    consume(envelope.getPayload());
  }

  // NOTE: event-service is not used here to consume event as it will obfuscate failure and is unable to express
  // NOTE: failure to consumer to allow features of DLQ to be used to re-process or otherwise capture failed events

  /**
   * Delegate consumption of event to {@link WorkEventHandler handlers}.
   *
   * All handlers will receive the given even if one or more of them throws an exception.
   *
   * @throws WorkEventConsumeException  If one or more handlers threw an exception.
   */
  private void consume(final WorkEvent event) {
    log.debug("Consume: {}", event);
    checkState(!handlers.isEmpty(), "At least one handler must be configured");

    // select handlers to handle event
    List<WorkEventHandler> selected = handlers.stream()
        .filter(h -> h.accept(event))
        .collect(Collectors.toList());
    checkState(!selected.isEmpty(), "No handlers accepted event");

    Map<WorkEventHandler,Throwable> failed = new LinkedHashMap<>();
    for (WorkEventHandler handler : selected) {
      log.debug("Handler: {}", handler);
      try {
        handler.handle(event);
      }
      catch (Exception e) {
        log.debug("Handler failed", e);
        failed.put(handler, e);
      }
    }

    if (!failed.isEmpty()) {
      throw new WorkEventConsumeException(event, failed);
    }
  }

  static class WorkEventConsumeException
    extends RuntimeException
  {
    public WorkEventConsumeException(final WorkEvent event, final Map<WorkEventHandler,Throwable> failed) {
      super(Plural.of(failed.keySet().size(), "handler") + " failed to handle work-event: " + event);
      failed.values().forEach(this::addSuppressed);
    }
  }
}
