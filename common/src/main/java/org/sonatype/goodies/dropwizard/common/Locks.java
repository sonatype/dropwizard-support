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
package org.sonatype.goodies.dropwizard.common;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import com.google.common.base.Throwables;

import static com.google.common.base.Preconditions.checkNotNull;

// see: https://github.com/sonatype/goodies/blob/master/common/src/main/java/org/sonatype/goodies/common/Locks.java

/**
 * Lock helpers.
 *
 * @since 1.2.0
 */
public class Locks
{
  private Locks() {
    // empty
  }

  /**
   * Returns locked lock.
   *
   * Uses {@link Lock#tryLock} with timeout of 60 seconds to avoid potential deadlocks.
   */
  public static Lock lock(final Lock lock) {
    checkNotNull(lock);
    try {
      if (!lock.tryLock(60, TimeUnit.SECONDS)) {
        throw new RuntimeException("Failed to obtain lock after 60 seconds");
      }
    }
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return lock;
  }

  /**
   * Returns locked read-lock.
   */
  public static Lock read(final ReadWriteLock guard) {
    checkNotNull(guard);
    return lock(guard.readLock());
  }

  @SuppressWarnings("Duplicates")
  public static <V> V readWith(final ReadWriteLock guard, final Callable<V> task) {
    checkNotNull(guard);
    checkNotNull(task);

    Lock lock = read(guard);
    try {
      return task.call();
    }
    catch (Exception e) {
      Throwables.throwIfUnchecked(e);
      throw new RuntimeException(e);
    }
    finally {
      lock.unlock();
    }
  }

  public static void readWith(final ReadWriteLock guard, final Runnable task) {
    checkNotNull(guard);
    checkNotNull(task);

    Lock lock = read(guard);
    try {
      task.run();
    }
    finally {
      lock.unlock();
    }
  }

  /**
   * Returns locked write-lock.
   */
  public static Lock write(final ReadWriteLock guard) {
    checkNotNull(guard);
    return lock(guard.writeLock());
  }

  @SuppressWarnings("Duplicates")
  public static <V> V writeWith(final ReadWriteLock guard, final Callable<V> task) {
    checkNotNull(guard);
    checkNotNull(task);

    Lock lock = write(guard);
    try {
      return task.call();
    }
    catch (Exception e) {
      Throwables.throwIfUnchecked(e);
      throw new RuntimeException(e);
    }
    finally {
      lock.unlock();
    }
  }

  public static void writeWith(final ReadWriteLock guard, final Runnable task) {
    checkNotNull(guard);
    checkNotNull(task);

    Lock lock = read(guard);
    try {
      task.run();
    }
    finally {
      lock.unlock();
    }
  }
}
