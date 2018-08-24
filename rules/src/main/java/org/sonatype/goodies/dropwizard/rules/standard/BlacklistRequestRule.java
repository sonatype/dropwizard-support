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
package org.sonatype.goodies.dropwizard.rules.standard;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response.Status;

import org.sonatype.goodies.dropwizard.rules.MatchRequestRule;
import org.sonatype.goodies.dropwizard.rules.RequestRule;
import org.sonatype.goodies.dropwizard.rules.RequestRuleResult;
import org.sonatype.goodies.dropwizard.rules.RequestRuleResults;
import org.sonatype.goodies.dropwizard.rules.matcher.request.RequestMatcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Blacklist {@link RequestRule}.
 *
 * @since ???
 */
@JsonTypeName(BlacklistRequestRule.TYPE)
public class BlacklistRequestRule
    extends MatchRequestRule
{
  public static final String TYPE = "blacklist";

  public static final Status DEFAULT_STATUS = Status.FORBIDDEN;

  public static final String DEFAULT_REASON = "Blacklisted";

  private final Status status;

  private final String reason;

  @JsonCreator
  public BlacklistRequestRule(@NotNull @JsonProperty("matchers") List<RequestMatcher> matchers,
                              @Nullable @JsonProperty("status") final Status status,
                              @Nullable @JsonProperty("reason") String reason)
  {
    super(TYPE, matchers);
    this.status = status != null ? status : DEFAULT_STATUS;
    log.debug("Status: {}", status);
    this.reason = reason != null ? reason : DEFAULT_REASON;
    log.debug("Reason: {}", reason);
  }

  // TODO: add support for metrics to count matched

  @Nonnull
  @Override
  protected RequestRuleResult matched(final RequestMatcher matcher, final HttpServletRequest request) {
    return RequestRuleResults.sendError(status, reason);
  }
}
