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
package org.sonatype.goodies.dropwizard.ratelimit

import com.google.common.base.Ticker
import io.dropwizard.util.Duration
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link LeakyBucket}.
 */
class LeakyBucketTest
{
  private class AdjustableTicker
    extends Ticker
  {
    private long value = 0L

    @Override
    long read() {
      return value
    }

    void advance(final Duration duration) {
      value += duration.toNanoseconds()
    }
  }

  private AdjustableTicker ticker

  @Before
  void setUp() {
    ticker = new AdjustableTicker()
  }

  @Test
  void 'bucket overflow'() {
    def underTest = new LeakyBucket(ticker, 3, 1, Duration.hours(1))
    assert underTest.capacity == 3
    assert underTest.size == 0

    assert !underTest.fill()
    assert underTest.size == 1

    assert !underTest.fill()
    assert underTest.size == 2

    assert !underTest.fill()
    assert underTest.size == 3

    assert underTest.fill() // overflown
    assert underTest.size == 3
  }

  @Test
  void 'bucket drain'() {
    def underTest = new LeakyBucket(ticker, 3, 1, Duration.seconds(1))
    assert underTest.capacity == 3
    assert underTest.size == 0

    assert !underTest.fill()
    assert underTest.size == 1

    assert !underTest.fill()
    assert underTest.size == 2

    assert !underTest.fill()
    assert underTest.size == 3

    assert underTest.fill() // overflow
    assert underTest.size == 3

    ticker.advance(Duration.seconds(1))

    assert !underTest.fill()
    assert underTest.size == 3

    assert underTest.fill() // overflow
    assert underTest.size == 3

    ticker.advance(Duration.seconds(2))

    assert underTest.drain() == 2
    assert underTest.size == 1
  }
}
