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
package org.sonatype.goodies.dropwizard.events;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.google.common.eventbus.EventBus;
import com.google.inject.Key;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.eclipse.sisu.inject.BeanLocator;

import static com.google.common.base.Preconditions.checkNotNull;

// see: https://github.com/sonatype/nexus-public/blob/master/components/nexus-base/src/main/java/org/sonatype/nexus/internal/event/EventManagerImpl.java

/**
 * Default {@link EventService}.
 *
 * @since ???
 */
@Named
@Singleton
public class EventServiceImpl
  extends ServiceSupport
  implements EventService
{
  // TODO: consider if we want to expose the fancy affinity logic that exists in NXRM implementation?

  private final BeanLocator beanLocator;

  private final EventBus synchronous;

  private final EventBus asynchronous;

  @Inject
  public EventServiceImpl(final BeanLocator beanLocator,
                          final EventBusFactory eventBusFactory,
                          final EventExecutor eventExecutor)
  {
    this.beanLocator = checkNotNull(beanLocator);
    checkNotNull(eventBusFactory);
    checkNotNull(eventExecutor);
    this.synchronous = eventBusFactory.create("dropwizard");
    log.debug("Synchronous: {}", synchronous);
    this.asynchronous = eventBusFactory.create("dropwizard-async", eventExecutor);
    log.debug("Asynchronous: {}", asynchronous);
  }

  /**
   * Mediator to register and unregister {@link EventAware} components.
   */
  private static class EventAwareMediator
      implements Mediator<Named, EventAware, EventServiceImpl>
  {
    @Override
    public void add(final BeanEntry<Named, EventAware> entry, final EventServiceImpl watcher) {
      watcher.register(entry.getValue());
    }

    @Override
    public void remove(final BeanEntry<Named, EventAware> entry, final EventServiceImpl watcher) {
      watcher.unregister(entry.getValue());
    }
  }

  @Override
  protected void doStarted() throws Exception {
    beanLocator.watch(Key.get(EventAware.class, Named.class), new EventAwareMediator(), this);
  }

  @Override
  public void register(final Object handler) {
    checkNotNull(handler);
    ensureStarted();

    boolean async = handler instanceof EventAware.Asynchronous;
    if (async) {
      asynchronous.register(handler);
    }
    else {
      synchronous.register(handler);
    }

    log.debug("Registered{}: {}", async ? " asynchronous" : "", handler);
  }

  @Override
  public void unregister(final Object handler) {
    checkNotNull(handler);
    ensureStarted();

    boolean async = handler instanceof EventAware.Asynchronous;
    if (async) {
      asynchronous.unregister(handler);
    }
    else {
      synchronous.unregister(handler);
    }

    log.debug("Unregistered{}: {}", async ? " asynchronous" : "", handler);
  }

  @Override
  public void post(final Object event) {
    checkNotNull(event);
    ensureStarted();

    log.trace("Posting: {}", event);
    synchronous.post(event);
    asynchronous.post(event);
  }
}
