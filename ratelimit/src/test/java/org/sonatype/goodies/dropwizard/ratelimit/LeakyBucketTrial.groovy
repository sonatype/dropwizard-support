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
import org.junit.jupiter.api.Test

/**
 * Leaky-bucket trials.
 */
class LeakyBucketTrial
{
  private static final Ticker ticker = Ticker.systemTicker()

  @Test
  void 'slow drip'() {
    def bucket = new LeakyBucket(ticker, 8, 1, Duration.seconds(1))
    log bucket

    (1..25).each {
      log it
      boolean overflown = bucket.fill()
      log bucket
      if (overflown) {
        log "Overflown"
      }
      Thread.sleep(1500)
    }
  }

  @Test
  void 'spiked drink'() {
    def bucket = new LeakyBucket(ticker, 8, 1, Duration.seconds(1))
    log bucket

    (1..9).each {
      log it
      boolean overflown = bucket.fill()
      log bucket
      if (overflown) {
        log "Overflown"
      }
    }
  }
}
