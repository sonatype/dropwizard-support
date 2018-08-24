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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.sonatype.goodies.dropwizard.rules.matcher.request.RequestMatcher;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Support for {@link RequestRule} that applies a list of {@link RequestMatcher matchers}.
 *
 * @since ???
 */
public abstract class MatchRequestRule
  implements RequestRule
{
  private static final Logger log = LoggerFactory.getLogger(MatchRequestRule.class);

  protected final String type;

  protected final RequestMatcher[] matchers;

  public MatchRequestRule(final String type, final List<RequestMatcher> matchers) {
    this.type = checkNotNull(type);
    checkNotNull(matchers);
    checkState(!matchers.isEmpty(), "At least one matcher is required");
    log.debug("Matchers: {}", matchers);
    this.matchers = matchers.toArray(new RequestMatcher[0]);
  }

  /**
   * Returns all matcher.
   */
  public List<RequestMatcher> getMatchers() {
    return ImmutableList.copyOf(matchers);
  }

  /**
   * Returns a matcher of given type or {@code null} if there is none.
   */
  @SuppressWarnings("unchecked")
  @Nullable
  public <T extends RequestMatcher> T getMatcher(final Class<T> type) {
    checkNotNull(type);

    for (RequestMatcher matcher : matchers) {
      if (type.isAssignableFrom(matcher.getClass())) {
        return (T) matcher;
      }
    }

    return null;
  }

  /**
   * Returns a list of matchers of the given type.
   */
  @SuppressWarnings("unchecked")
  public <T extends RequestMatcher> List<T> getMatchers(final Class<T> type) {
    checkNotNull(type);

    List<T> result = new ArrayList<>();
    for (RequestMatcher matcher : matchers) {
      if (type.isAssignableFrom(matcher.getClass())) {
        result.add((T)matcher);
      }
    }

    return result;
  }

  @Nullable
  @Override
  public RequestRuleResult evaluate(final HttpServletRequest request) {
    checkNotNull(request);

    log.debug("Matching: {}", request);

    for (int i=0; i<matchers.length; i++) {
      RequestMatcher matcher = matchers[i];
      log.debug("Matcher[{}]: {}", i, matcher);

      if (matcher.matches(request)) {
        return matched(matcher, request);
      }
    }

    return null;
  }

  /**
   * Create result for matched request.
   */
  @Nonnull
  protected abstract RequestRuleResult matched(final RequestMatcher matcher, final HttpServletRequest request);

  @Override
  public String toString() {
    return String.format("%s{%s}", type, ImmutableList.copyOf(matchers));
  }
}
