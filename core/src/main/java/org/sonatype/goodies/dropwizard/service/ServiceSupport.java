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
package org.sonatype.goodies.dropwizard.service;

import org.sonatype.goodies.dropwizard.logging.Loggers;

import com.google.common.base.Throwables;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkState;

/**
 * Support for {@link Managed} services.
 *
 * @since 1.0.0
 */
public abstract class ServiceSupport
    implements Managed
{
  protected final Logger log = Loggers.getLogger(getClass());

  private volatile boolean started;

  protected void ensureStarted() {
    checkState(started, "Not started");
  }

  /**
   * Start service if not already started.
   */
  @Override
  public final void start() throws Exception {
    checkState(!started, "Already started");
    synchronized (this) {
      if (!started) {
        doStart();
        started = true;
        log.debug("Started");
        try {
          doStarted();
        }
        catch (Exception e) {
          log.warn("Started hook failed", e);
        }
      }
    }
  }

  /**
   * Custom service start logic.
   */
  protected void doStart() throws Exception {
    // empty
  }

  /**
   * Custom service started logic.
   *
   * This is called after {@link #doStart()}, but after the started flag has been set.
   *
   * @since 1.2.0
   */
  protected void doStarted() throws Exception {
    // empty
  }

  /**
   * Stop service if started.  Duplicate stop is ignored.
   */
  @Override
  public final void stop() throws Exception {
    if (started) {
      synchronized (this) {
        if (started) {
          doStop();
          started = false;
          log.debug("Stopped");
          try {
            doStopped();
          }
          catch (Exception e) {
            log.warn("Stopped hook failed", e);
          }
        }
      }
    }
  }

  /**
   * Custom service stop logic.
   */
  protected void doStop() throws Exception {
    // empty
  }

  /**
   * Custom service stopped logic.
   *
   * This is called after {@link #doStop()}, but after the started flag has been unset.
   *
   * @since 1.2.0
   */
  protected void doStopped() throws Exception {
    // empty
  }

  /**
   * Propagate given exception.
   */
  protected RuntimeException propagateException(final Exception e) {
    log.error("Service failure", e);
    Throwables.throwIfUnchecked(e);
    throw new RuntimeException(e);
  }
}
