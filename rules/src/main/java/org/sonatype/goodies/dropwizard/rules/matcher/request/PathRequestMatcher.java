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
package org.sonatype.goodies.dropwizard.rules.matcher.request;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.sonatype.goodies.dropwizard.rules.matcher.string.StringMatcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Match {@link HttpServletRequest#getRequestURI() request path}.
 *
 * @since 1.2.0
 */
@JsonTypeName(PathRequestMatcher.TYPE)
public class PathRequestMatcher
    implements RequestMatcher
{
  public static final String TYPE = "path";

  private final StringMatcher matcher;

  @JsonCreator
  public PathRequestMatcher(@NotNull @JsonProperty("matcher") final StringMatcher matcher) {
    this.matcher = checkNotNull(matcher);
  }

  public StringMatcher getMatcher() {
    return matcher;
  }

  @Override
  public boolean matches(final HttpServletRequest request) {
    return matcher.matches(request.getRequestURI());
  }

  @Override
  public String toString() {
    return String.format("%s{%s}", TYPE, matcher);
  }
}
