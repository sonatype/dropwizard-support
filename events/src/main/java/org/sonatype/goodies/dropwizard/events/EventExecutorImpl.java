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

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.events.EventConfiguration.EventExecutorConfiguration;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.dropwizard.util.Duration;

import static com.google.common.base.Preconditions.checkNotNull;

// see: https://github.com/sonatype/nexus-public/blob/master/components/nexus-base/src/main/java/org/sonatype/nexus/internal/event/EventExecutor.java

/**
 * Default {@link EventExecutor}.
 *
 * @since ???
 */
@Named
@Singleton
@Priority(100_000)
public class EventExecutorImpl
    extends ServiceSupport
    implements EventExecutor
{
  private static final RejectedExecutionHandler CALLER_RUNS_FAILSAFE = (command, executor) -> command.run();

  private final EventExecutorConfiguration configuration;

  private ThreadPoolExecutor threadPool;

  @Inject
  public EventExecutorImpl(final EventConfiguration configuration) {
    checkNotNull(configuration);
    this.configuration = configuration.getEventExecutorConfiguration();
    log.debug("Configuration: {}", configuration);
  }

  @Override
  protected void doStart() throws Exception {
    Duration keepAlive = configuration.getKeepAlive();
    threadPool = new ThreadPoolExecutor(
        configuration.getCorePoolSize(),
        configuration.getMaximumPoolSize(),
        keepAlive.getQuantity(),
        keepAlive.getUnit(),
        new SynchronousQueue<>(configuration.isFairThreading()),
        new ThreadFactoryBuilder().setNameFormat("events-%d").build(),
        CALLER_RUNS_FAILSAFE
    );
    log.debug("Thread-pool: {}", threadPool);
  }

  @Override
  protected void doStop() throws Exception {
    threadPool.shutdown();
    Duration gracePeriod = configuration.getShutdownGracePeriod();
    log.debug("Awaiting termination: {}", gracePeriod);
    threadPool.awaitTermination(gracePeriod.getQuantity(), gracePeriod.getUnit());
    threadPool = null;
  }

  @Override
  public void execute(final Runnable command) {
    checkNotNull(command);
    ensureStarted();

    // TODO: expose security/mdc handling

    log.trace("Execute: {}", command);
    threadPool.execute(command);
  }
}
