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
  private final String name;

  protected final String value;

  protected final boolean ignoreCase;

  public StringMatcherSupport(final String name, final String value, final boolean ignoreCase) {
    this.name = checkNotNull(name);
    checkNotNull(value);
    this.value = ignoreCase ? MoreStrings.lower(value) : value;
    this.ignoreCase = ignoreCase;
  }

  protected abstract boolean doMatch(final String subject);

  @Override
  public boolean matches(final String string) {
    if (ignoreCase) {
      return doMatch(MoreStrings.lower(string));
    }
    return doMatch(string);
  }

  @Override
  public String toString() {
    return String.format("%s{'%s'%s}", name, value, ignoreCase ? " ignore-case" : "");
  }
}
