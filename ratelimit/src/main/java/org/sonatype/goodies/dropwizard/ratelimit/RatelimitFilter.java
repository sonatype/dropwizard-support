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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.StatusType;

import org.sonatype.goodies.dropwizard.jaxrs.StatusTypeFactory;
import org.sonatype.goodies.dropwizard.ratelimit.RatelimitConfiguration.RequestStrategy;
import org.sonatype.goodies.dropwizard.servlet.HttpFilterSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Rate-limit filter.
 *
 * @since ???
 * @see RatelimitService
 */
@Named
@Singleton
public class RatelimitFilter
    extends HttpFilterSupport
{
  public static final StatusType TOO_MANY_REQUESTS = StatusTypeFactory.create(429, "Too many requests");

  private final RatelimitService ratelimitService;

  @Inject
  public RatelimitFilter(final RatelimitService ratelimitService) {
    this.ratelimitService = checkNotNull(ratelimitService);
  }

  /**
   * Filter request.
   *
   * If whitelisted allow; otherwise resolve if a request-strategy for request exists.
   * If a strategy exists, then apply rate-limiting; else allow.
   */
  @Override
  protected void filter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
      throws IOException, ServletException
  {
    boolean allow = true;
    boolean whitelisted = ratelimitService.isWhitelisted(request.getRemoteAddr());

    if (!whitelisted) {
      // match request to strategy if we can
      RequestStrategy strategy = ratelimitService.strategy(request);
      log.trace("Strategy: {}", strategy);

      if (strategy != null) {
        // resolve user tracker for request
        RatelimitTracker tracker = ratelimitService.tracker(request);
        log.trace("Tracker: {}", tracker);

        boolean overflown = ratelimitService.tick(tracker, strategy);
        allow = !overflown;
      }
    }

    if (allow) {
      chain.doFilter(request, response);
    }
    else {
      response.sendError(TOO_MANY_REQUESTS.getStatusCode(), TOO_MANY_REQUESTS.getReasonPhrase());
    }
  }
}
