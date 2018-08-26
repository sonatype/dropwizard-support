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
package org.sonatype.goodies.dropwizard.rules.matcher.string;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * NOT {@link StringMatcher}.
 *
 * @since ???
 */
@JsonTypeName(NotStringMatcher.TYPE)
public class NotStringMatcher
    implements StringMatcher
{
  public static final String TYPE = "not";

  private final StringMatcher matcher;

  public NotStringMatcher(@NotNull @JsonProperty("matcher") final StringMatcher matcher) {
    this.matcher = checkNotNull(matcher);
  }

  public StringMatcher getMatcher() {
    return matcher;
  }

  @Override
  public boolean matches(@Nullable final String string) {
    return !matcher.matches(string);
  }

  @Override
  public String toString() {
    return String.format("%s{%s}", TYPE, matcher);
  }
}
