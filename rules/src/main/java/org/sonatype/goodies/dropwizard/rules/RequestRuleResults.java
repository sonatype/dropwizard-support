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
package org.sonatype.goodies.dropwizard.rules;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

/**
 * Helper to create standard {@link RequestRuleResult request-rule results}.
 *
 * @since ???
 */
public final class RequestRuleResults
{
  private RequestRuleResults() {
    // empty
  }

  //
  // send-error
  //

  public static RequestRuleResult sendError(final int code, final String reason) {
    return new RequestRuleResult()
    {
      @Override
      public void apply(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
          throws IOException, ServletException
      {
        response.sendError(code, reason);
      }

      @Override
      public String toString() {
        return String.format("send-error{%s,%s}", code, reason);
      }
    };
  }

  public static RequestRuleResult sendError(final Status status, final String reason) {
    return sendError(status.getStatusCode(), reason);
  }

  public static RequestRuleResult sendError(final Status status) {
    return sendError(status.getStatusCode(), status.getReasonPhrase());
  }

  //
  // continue-chain
  //

  public static RequestRuleResult continueChain() {
    return new RequestRuleResult() {
      @Override
      public void apply(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
          throws IOException, ServletException
      {
        chain.doFilter(request, response);
      }

      @Override
      public String toString() {
        return "continue-chain";
      }
    };
  }
}
