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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonatype.goodies.dropwizard.servlet.HttpFilterSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link RequestRuleService} filter.
 *
 * @since ???
 */
@Named
@Singleton
public class RequestRuleFilter
    extends HttpFilterSupport
{
  private final RequestRuleService ruleService;

  @Inject
  public RequestRuleFilter(final RequestRuleService ruleService) {
    this.ruleService = checkNotNull(ruleService);
  }

  @Override
  protected void filter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
      throws IOException, ServletException
  {
    RequestRuleResult result = ruleService.evaluate(request);
    if (result != null) {
      result.apply(request, response, chain);
    }
    else {
      chain.doFilter(request, response);
    }
  }
}
