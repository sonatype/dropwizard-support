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
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import static com.google.common.base.Preconditions.checkNotNull;

// see: https://github.com/sonatype/nexus-public/blob/master/components/nexus-base/src/main/java/org/sonatype/nexus/internal/event/EventExecutor.java

/**
 * Default {@link EventExecutor}.
 *
 * @since ???
 */
@Named
@Singleton
public class EventExecutorImpl
    extends ServiceSupport
    implements EventExecutor
{
  private static final RejectedExecutionHandler CALLER_RUNS_FAILSAFE = (command, executor) -> command.run();

  private final EventConfiguration configuration;

  private ThreadPoolExecutor threadPool;

  @Inject
  public EventExecutorImpl(final EventConfiguration configuration) {
    this.configuration = checkNotNull(configuration);
  }

  @Override
  protected void doStart() throws Exception {
    // TODO: expose configuration

    threadPool = new ThreadPoolExecutor(
        0,
        50,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(false),
        new ThreadFactoryBuilder().setNameFormat("events-%d").build(),
        CALLER_RUNS_FAILSAFE
    );

    // TODO: expose security/mdc handling
  }

  @Override
  protected void doStop() throws Exception {
    threadPool.shutdown();
    threadPool.awaitTermination(5, TimeUnit.SECONDS);
    threadPool = null;
  }

  @Override
  public void execute(final Runnable command) {
    checkNotNull(command);
    ensureStarted();

    threadPool.execute(command);
  }
}
