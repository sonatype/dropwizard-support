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
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.sonatype.goodies.dropwizard.rules.MatchRequestRule;
import org.sonatype.goodies.dropwizard.rules.RequestRule;
import org.sonatype.goodies.dropwizard.rules.RequestRuleResult;
import org.sonatype.goodies.dropwizard.rules.RequestRuleResults;
import org.sonatype.goodies.dropwizard.rules.matcher.request.RequestMatcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static com.google.common.base.Preconditions.checkNotNull;

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

  public static final StatusType DEFAULT_STATUS = Status.FORBIDDEN;

  public static final String DEFAULT_REASON = "Blacklisted";

  @JsonProperty
  private StatusType status = DEFAULT_STATUS;

  @JsonProperty
  private String reason = DEFAULT_REASON;

  @JsonCreator
  public BlacklistRequestRule(@NotNull @JsonProperty("matchers") final List<RequestMatcher> matchers) {
    super(TYPE, matchers);
    this.status = status != null ? status : DEFAULT_STATUS;
  }

  public StatusType getStatus() {
    return status;
  }

  public void setStatus(final Status status) {
    this.status = checkNotNull(status);
  }

  public String getReason() {
    return reason;
  }

  public void setReason(final String reason) {
    this.reason = checkNotNull(reason);
  }

  @Nonnull
  @Override
  protected RequestRuleResult matched(final RequestMatcher matcher, final HttpServletRequest request) {
    return RequestRuleResults.sendError(status, reason);
  }
}
