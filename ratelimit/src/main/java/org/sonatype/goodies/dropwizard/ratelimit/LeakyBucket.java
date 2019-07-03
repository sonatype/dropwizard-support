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
package org.sonatype.goodies.dropwizard.ratelimit;

import com.google.common.base.MoreObjects;
import com.google.common.base.Ticker;
import io.dropwizard.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Leaky bucket algorithm.
 *
 * Bucket starts out empty with a {@link #capacity}, and a drain {@link #drainCountPerPeriod count-per-period} and
 * {@link #drainPeriodNanos period}.
 *
 * Drops (units of work or requests or whatever) are added to the bucket.
 * Before drops are added, the bucket is potentially drained based on configured rate.
 *
 * If the bucket is ever filled with more drops than {@link #capacity} then the bucket is considered overflown.
 * Overflown drops are all discarded, even if a portion of drops could fit before overflowing with {@link #fill(long)}.
 *
 * @since 1.2.0
 */
public class LeakyBucket
{
  private static final Logger log = LoggerFactory.getLogger(LeakyBucket.class);

  /**
   * Ticker to track time.
   */
  private final Ticker ticker;

  /**
   * The total capacity of the bucket.
   */
  private final long capacity;

  /**
   * How many drops to drain per-period.
   *
   * @see #drainPeriodNanos
   */
  private final long drainCountPerPeriod;

  /**
   * The period at which drops drain in nano-seconds.
   */
  private final long drainPeriodNanos;

  /**
   * The current number of drops in the bucket.
   */
  private long size = 0;

  /**
   * The time the bucket was last drained.
   */
  private long lastDrainTime;

  /**
   * The next time the bucket may be drained.
   */
  private long nextDrainTime;

  public LeakyBucket(final Ticker ticker,
                     final long capacity,
                     final long drainCountPerPeriod,
                     final Duration drainPeriod)
  {
    this.ticker = checkNotNull(ticker);
    checkArgument(capacity > 0);
    this.capacity = capacity;
    checkArgument(drainCountPerPeriod > 0);
    this.drainCountPerPeriod = drainCountPerPeriod;
    this.drainPeriodNanos = checkNotNull(drainPeriod).toNanoseconds();

    // set initial last+next drain-times
    long now = ticker.read();
    lastDrainTime = now;
    nextDrainTime = now + drainPeriodNanos;
  }

  /**
   * Returns bucket capacity.
   */
  public long getCapacity() {
    return capacity;
  }

  /**
   * Get current size (number of drops) in bucket.
   */
  public long getSize() {
    return size;
  }

  /**
   * Attempt to drain bucket based on constant rate expressed by {@link #drainCountPerPeriod} and {@link #drainPeriodNanos}.
   *
   * @return Drops drained.
   */
  public long drain() {
    long now = ticker.read();

    // skip if not ready to drain yet
    if (now < nextDrainTime) {
      return 0;
    }

    // calculate how many periods have been missed
    long missedPeriods = Math.max(0, (now - lastDrainTime) / drainPeriodNanos);

    // adjust last drain-time; calculate the target time based on now many periods we missed since last drained
    lastDrainTime += missedPeriods * drainPeriodNanos;

    // adjust next drain-time
    nextDrainTime = lastDrainTime + drainPeriodNanos;

    // calculate how many to drain (adjusted for max available to drain)
    long drainCount = Math.min(size, missedPeriods * drainCountPerPeriod);
    size -= drainCount;
    log.trace("Drained: {}; new-size: {}", drainCount, size);

    return drainCount;
  }

  /**
   * Attempt to fill bucket.
   *
   * Before filling bucket is checked for potential drainage.
   *
   * @param count   Number of drops to add to bucket.
   * @return        {@code true} if adding count drops to bucket would overflow.
   *                All (including non-overflowing) drops are discarded.
   */
  public boolean fill(final long count) {
    checkArgument(count > 0);

    // first attempt to drain
    drain();

    // calculate the target updated size with count drops
    long updated = size + count;
    if (updated > capacity) {
      // bucket would overflow if count drops were added
      return true;
    }

    log.trace("Fill: {}", updated);
    size = updated;
    return false;
  }

  /**
   * Attempt to fill bucket by one.
   *
   * @see #fill(long)
   */
  public boolean fill() {
    return fill(1);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("capacity", capacity)
        .add("drainCountPerPeriod", drainCountPerPeriod)
        .add("drainPeriodNanos", drainPeriodNanos)
        .add("size", size)
        .add("lastDrainTime", lastDrainTime)
        .add("nextDrainTime", nextDrainTime)
        .toString();
  }
}
