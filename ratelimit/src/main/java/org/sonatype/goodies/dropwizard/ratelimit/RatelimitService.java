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

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.sonatype.goodies.dropwizard.ratelimit.RatelimitConfiguration.RequestStrategy;
import org.sonatype.goodies.dropwizard.common.ip.IpAddresses;

/**
 * Rate-limit service
 *
 * @since 1.2.0
 */
public interface RatelimitService
{
  IpAddresses getWhitelist();

  /**
   * Check if given address is whitelisted.
   */
  boolean isWhitelisted(String address);

  /**
   * Attempt to match strategy for request.
   */
  @Nullable
  RequestStrategy strategy(HttpServletRequest request);

  /**
   * Lookup or create a user tracker for given request.
   */
  RatelimitTracker tracker(HttpServletRequest request);

  /**
   * Apply one tick to tracker.
   *
   * @return {@code true} if bucket has overflown.
   */
  boolean tick(RatelimitTracker tracker, RequestStrategy strategy);
}
