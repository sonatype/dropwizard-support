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

import java.lang.reflect.Method;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Logging {@link SubscriberExceptionHandler}.
 *
 * @since 1.2.0
 */
public class LoggingSubscriberExceptionHandler
    implements SubscriberExceptionHandler
{
  private final Logger logger;

  public LoggingSubscriberExceptionHandler(final Logger logger) {
    this.logger = checkNotNull(logger);
  }

  @Override
  public void handleException(final Throwable exception, final SubscriberExceptionContext context) {
    if (logger.isErrorEnabled()) {
      // aligns with https://github.com/google/guava/blob/master/guava/src/com/google/common/eventbus/EventBus.java#L240
      Method method = context.getSubscriberMethod();
      logger.error("Exception thrown by subscriber method {}({}) on subscriber {} when dispatching event: {}",
          method.getName(),
          method.getParameterTypes()[0].getName(),
          context.getSubscriber(),
          context.getEvent(),
          exception
      );
    }
  }
}
