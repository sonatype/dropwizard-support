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

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkState;

/**
 * Support for lifecycle components.
 *
 * @since 1.2.0
 */
@Beta
public abstract class LifecycleSupport
{
  protected final Logger log = Loggers.getLogger(getClass());

  private volatile boolean started;

  protected void ensureStarted() {
    checkState(started, "Not started");
  }

  /**
   * Start if not already started.
   *
   * @throws IllegalStateException  Already started
   */
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
   * Custom start behavior.
   */
  protected void doStart() throws Exception {
    // empty
  }

  /**
   * Custom started behavior.
   *
   * This is called after {@link #doStart()}, but after the started flag has been set.
   *
   * @since 1.2.0
   */
  protected void doStarted() throws Exception {
    // empty
  }

  /**
   * Stop if started.  Duplicate stop is ignored.
   */
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
   * Custom stop behavior.
   */
  protected void doStop() throws Exception {
    // empty
  }

  /**
   * Custom stopped behavior.
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
    log.error("Lifecycle failure", e);
    Throwables.throwIfUnchecked(e);
    throw new RuntimeException(e);
  }
}
