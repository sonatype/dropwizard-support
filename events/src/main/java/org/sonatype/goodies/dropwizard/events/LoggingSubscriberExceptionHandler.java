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

import org.sonatype.goodies.dropwizard.util.ThrowableHelper;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

// see: https://github.com/sonatype/nexus-public/blob/master/components/nexus-common/src/main/java/org/sonatype/nexus/common/event/Slf4jSubscriberExceptionHandler.java

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
  public void handleException(final Throwable cause, final SubscriberExceptionContext context) {
    logger.error("Could not dispatch event {} to subscriber {} method [{}]: {}",
        context.getEvent(),
        context.getSubscriber(),
        context.getSubscriberMethod(),
        ThrowableHelper.explain(cause),
        cause
    );
  }
}
