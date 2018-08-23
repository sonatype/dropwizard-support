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

import java.util.List;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response.Status;

import org.sonatype.goodies.dropwizard.rules.matcher.request.RequestMatcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Blacklist {@link RequestRule}.
 *
 * @since ???
 */
@JsonTypeName(BlacklistRequestRule.NAME)
public class BlacklistRequestRule
    extends MatchRequestRule
{
  public static final String NAME = "blacklist";

  public static final String DEFAULT_REASON = "Blacklisted";

  private final String reason;

  @JsonCreator
  public BlacklistRequestRule(@NotNull @JsonProperty("matchers") List<RequestMatcher> matchers,
                              @Nullable @JsonProperty("reason") String reason)
  {
    super(matchers);
    this.reason = reason != null ? reason : DEFAULT_REASON;
  }

  @Nullable
  @Override
  public RuleResult evaluate(final HttpServletRequest request) {
    checkNotNull(request);

    for (RequestMatcher matcher : matchers) {
      if (matcher.match(request)) {
        return RuleResults.sendError(Status.FORBIDDEN, reason);
      }
    }

    return null;
  }

  @Override
  public String toString() {
    return String.format("%s{%s}", NAME, ImmutableList.copyOf(matchers));
  }
}
