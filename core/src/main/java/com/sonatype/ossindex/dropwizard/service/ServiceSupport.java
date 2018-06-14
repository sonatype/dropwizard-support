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
package com.sonatype.ossindex.dropwizard.service;

import com.google.common.base.Throwables;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkState;

/**
 * Support for {@link Managed} services.
 *
 * @since ???
 */
public abstract class ServiceSupport
    implements Managed
{
  protected final Logger log = LoggerFactory.getLogger(getClass());

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
   * Stop service if started.  Duplicate stop is ignored.
   */
  @Override
  public final void stop() throws Exception {
    if (started) {
      synchronized (this) {
        if (started) {
          doStop();
          log.debug("Stopped");
          started = false;
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
   * Propagate given exception.
   */
  protected RuntimeException propagateException(final Exception e) {
    log.error("Service failure", e);
    Throwables.throwIfUnchecked(e);
    throw new RuntimeException(e);
  }
}
