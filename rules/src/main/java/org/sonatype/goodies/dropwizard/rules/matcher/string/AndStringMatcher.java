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

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * AND {@link StringMatcher}.
 *
 * @since ???
 */
@JsonTypeName("and")
public class AndStringMatcher
    implements StringMatcher
{
  private final List<StringMatcher> matchers;

  public AndStringMatcher(@NotNull @JsonProperty("matchers") final List<StringMatcher> matchers) {
    this.matchers = checkNotNull(matchers);
  }

  @Override
  public boolean match(final String value) {
    for (StringMatcher matcher : matchers) {
      if (!matcher.match(value)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public String toString() {
    return String.format("and{%s}", matchers);
  }
}
