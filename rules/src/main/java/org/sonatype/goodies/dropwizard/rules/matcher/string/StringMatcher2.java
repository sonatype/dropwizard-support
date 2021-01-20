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

import org.sonatype.goodies.dropwizard.util.MoreStrings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.annotations.Beta;

import static com.google.common.base.Preconditions.checkNotNull;

// HACK: experimenting with other means to make matchers; more concise impl but more verbose configuration :-\

/**
 * ???
 *
 * @since 1.2.0
 */
@Beta
@JsonTypeName(StringMatcher2.TYPE)
public class StringMatcher2
    implements StringMatcher
{
  public static final String TYPE = "string2";

  enum Function
  {
    contains() {
      @Override
      public boolean matches(final String basis, final String value) {
        return value.contains(basis);
      }
    },
    prefix() {
      @Override
      public boolean matches(final String basis, final String value) {
        return value.startsWith(basis);
      }
    },
    suffix() {
      @Override
      public boolean matches(final String basis, final String value) {
        return value.endsWith(basis);
      }
    };

    public abstract boolean matches(final String basis, final String value);
  }

  private final Function function;

  private final String value;

  private final boolean ignoreCase;

  public StringMatcher2(@NotNull @JsonProperty("function") final Function function,
                        @NotNull @JsonProperty("value") final String value,
                        @JsonProperty("ignoreCase") final boolean ignoreCase)
  {
    this.function = checkNotNull(function);
    checkNotNull(value);
    this.value = ignoreCase ? MoreStrings.lower(value) : value;
    this.ignoreCase = ignoreCase;
  }

  public Function getFunction() {
    return function;
  }

  public String getValue() {
    return value;
  }

  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  /**
   * Matches given string.
   *
   * If {@link #ignoreCase} then the given string is converted to lower-case before evaluating function.
   */
  @Override
  public boolean matches(@Nullable final String string) {
    if (string == null) {
      return false;
    }
    if (ignoreCase) {
      return function.matches(value, MoreStrings.lower(string));
    }
    return function.matches(value, string);
  }

  @Override
  public String toString() {
    return String.format("%s{'%s'%s}", function, value, ignoreCase ? " ignore-case" : "");
  }
}
