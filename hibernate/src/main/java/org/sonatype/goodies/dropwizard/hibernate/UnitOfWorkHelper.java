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
package org.sonatype.goodies.dropwizard.hibernate;

import java.util.concurrent.Callable;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link UnitOfWork}-aware helper.
 *
 * @since 1.2.0
 */
@Beta
public class UnitOfWorkHelper
{
  private static final Logger log = LoggerFactory.getLogger(UnitOfWorkHelper.class);

  @UnitOfWork
  public <V> V apply(final Callable<V> task) {
    checkNotNull(task);
    log.trace("Apply: {}", task);
    try {
      V result = task.call();
      log.trace("Result: {}", result);
      return result;
    }
    catch (Exception e) {
      log.trace("Failed", e);
      Throwables.throwIfUnchecked(e);
      throw new RuntimeException(e);
    }
  }

  @UnitOfWork
  public void apply(final Runnable task) {
    checkNotNull(task);
    log.trace("Apply: {}", task);
    try {
      task.run();
    }
    catch (Exception e) {
      log.trace("Failed", e);
      Throwables.throwIfUnchecked(e);
      throw new RuntimeException(e);
    }
  }
}
