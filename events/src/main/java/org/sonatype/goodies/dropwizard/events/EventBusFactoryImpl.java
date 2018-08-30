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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.LoggerFactory;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

// see: https://github.com/sonatype/nexus-public/blob/master/components/nexus-common/src/main/java/org/sonatype/nexus/common/event/EventBusFactory.java

/**
 * Default {@link EventBusFactory}.
 *
 * @since ???
 */
@Named
@Singleton
public class EventBusFactoryImpl
  implements EventBusFactory
{
  private EventBusFactoryImpl() {
    // empty
  }

  @Override
  public EventBus create(final String name) {
    return newEventBus(name, directExecutor());
  }

  @Override
  public EventBus create(final String name, final Executor executor) {
    return newEventBus(name, executor);
  }

  private static EventBus newEventBus(final String name, final Executor executor) {
    try {
      Class<?> dispatcherClass = EventBus.class.getClassLoader().loadClass("com.google.common.eventbus.Dispatcher");

      // immediate dispatcher means events are always processed in a reentrant fashion
      Method immediateDispatcherMethod = dispatcherClass.getDeclaredMethod("immediate");
      immediateDispatcherMethod.setAccessible(true);

      // EventBus constructor that accepts custom executor is not yet part of the public API
      Constructor<EventBus> factory = EventBus.class.getDeclaredConstructor(
          String.class, Executor.class, dispatcherClass, SubscriberExceptionHandler.class);
      factory.setAccessible(true);

      Object immediateDispatcher = immediateDispatcherMethod.invoke(null);

      SubscriberExceptionHandler exceptionHandler = new LoggingSubscriberExceptionHandler(
          LoggerFactory.getLogger(String.format("%s.%s", EventBus.class.getName(), name))
      );

      return factory.newInstance(name, executor, immediateDispatcher, exceptionHandler);
    }
    catch (Exception e) {
      Throwables.throwIfUnchecked(e);
      throw new LinkageError("Unable to create EventBus with custom executor", e);
    }
  }
}
