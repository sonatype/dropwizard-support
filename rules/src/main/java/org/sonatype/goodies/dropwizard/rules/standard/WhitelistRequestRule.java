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

import org.sonatype.goodies.dropwizard.rules.MatchRequestRule;
import org.sonatype.goodies.dropwizard.rules.RequestRule;
import org.sonatype.goodies.dropwizard.rules.RequestRuleResult;
import org.sonatype.goodies.dropwizard.rules.RequestRuleResults;
import org.sonatype.goodies.dropwizard.rules.matcher.request.RequestMatcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Whitelist {@link RequestRule}.
 *
 * When used together with {@link BlacklistRequestRule blacklist-rule} this should be configured first.
 *
 * @since ???
 */
@JsonTypeName(WhitelistRequestRule.TYPE)
public class WhitelistRequestRule
    extends MatchRequestRule
{
  public static final String TYPE = "whitelist";

  @JsonCreator
  public WhitelistRequestRule(@NotNull @JsonProperty("matchers") final List<RequestMatcher> matchers) {
    super(TYPE, matchers);
  }

  @Nonnull
  @Override
  protected RequestRuleResult matched(final RequestMatcher matcher, final HttpServletRequest request) {
    return RequestRuleResults.continueChain();
  }
}
