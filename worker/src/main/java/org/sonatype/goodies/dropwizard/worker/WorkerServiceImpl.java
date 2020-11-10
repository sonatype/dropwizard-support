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

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.events.EventAware;
import org.sonatype.goodies.dropwizard.events.EventService;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;
import org.sonatype.goodies.dropwizard.util.ManagedHelper;
import org.sonatype.goodies.dropwizard.worker.internal.SnsEventProducerConfiguration;
import org.sonatype.goodies.dropwizard.worker.internal.SqsEventConsumerConfiguration;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import static com.google.common.base.Preconditions.checkNotNull;

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
  private final EventService eventService;

  private final WorkEventEnvelopeProducer.Factory producerFactory;

  private final WorkEventEnvelopeConsumer.Factory consumerFactory;

  private final WorkerServiceConfiguration configuration;

  @Nullable
  private WorkEventEnvelopeProducer producer;

  @Nullable
  private WorkEventEnvelopeConsumer consumer;

  @Inject
  public WorkerServiceImpl(final EventService eventService,
                           final WorkEventEnvelopeProducer.Factory producerFactory,
                           final WorkEventEnvelopeConsumer.Factory consumerFactory,
                           final WorkerServiceConfiguration configuration)
  {
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
      consumer = consumerFactory.create(consumerConfiguration, (envelope -> consume(envelope.getPayload())));
      log.debug("Consumer: {}", consumer);
    }

    ManagedHelper.start(consumer);
    ManagedHelper.start(producer);

    eventService.register(this);
  }

  @Override
  protected void doStop() throws Exception {
    eventService.unregister(this);

    ManagedHelper.stop(producer);
    producer = null;
    ManagedHelper.stop(consumer);
    consumer = null;
  }

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
      eventService.post(new ConsumeWorkEvent(event));
    }
  }

  @Subscribe
  @AllowConcurrentEvents
  public void on(final ConsumeWorkEvent event) {
    checkNotNull(event);
    ensureStarted();
    log.debug("ON: {}", event);

    consume(event.getEvent());
  }

  private void consume(final WorkEvent event) {
    log.debug("Consume: {}", event);

    eventService.post(event);
  }
}
