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

import org.sonatype.goodies.dropwizard.util.MoreStrings;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for {@link StringMatcher} implementations.
 *
 * @since ???
 */
public abstract class StringMatcherSupport
    implements StringMatcher
{
  protected final String type;

  protected final String value;

  protected final boolean ignoreCase;

  public StringMatcherSupport(final String type, final String value, final boolean ignoreCase) {
    this.type = checkNotNull(type);
    checkNotNull(value);
    this.value = ignoreCase ? MoreStrings.lower(value) : value;
    this.ignoreCase = ignoreCase;
  }

  public String getValue() {
    return value;
  }

  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  /**
   * Check if given string matches.
   */
  protected abstract boolean doMatch(final String subject);

  /**
   * Matches given string.
   *
   * If {@link #ignoreCase} then the given string is converted to lower-case before calling {@link #doMatch(String)}.
   */
  @Override
  public boolean matches(@Nullable final String string) {
    if (string == null) {
      return false;
    }
    if (ignoreCase) {
      return doMatch(MoreStrings.lower(string));
    }
    return doMatch(string);
  }

  @Override
  public String toString() {
    return String.format("%s{'%s'%s}", type, value, ignoreCase ? " ignore-case" : "");
  }
}
