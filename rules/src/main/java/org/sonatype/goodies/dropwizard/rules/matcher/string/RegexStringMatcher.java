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

import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.annotations.VisibleForTesting;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Match if string matches regular-expression.
 *
 * @since ???
 */
@JsonTypeName(RegexStringMatcher.TYPE)
public class RegexStringMatcher
    implements StringMatcher
{
  public static final String TYPE = "regex";

  private final String pattern;

  @Nullable
  private Pattern compiled;

  private int flags = 0;

  @JsonCreator
  public RegexStringMatcher(@NotNull @JsonProperty("pattern") final String pattern) {
    this.pattern = checkNotNull(pattern);
  }

  private void flag(final int flag, final boolean enable) {
    if (enable) {
      flags = flags | flag;
    }
  }

  @JsonProperty
  public void setCanonEq(final boolean enable) {
    flag(Pattern.CANON_EQ, enable);
  }

  @JsonProperty
  public void setIgnoreCase(final boolean enable) {
    flag(Pattern.CASE_INSENSITIVE, enable);
  }

  @JsonProperty
  public void setComments(final boolean enable) {
    flag(Pattern.COMMENTS, enable);
  }

  @JsonProperty
  public void setDotAll(final boolean enable) {
    flag(Pattern.DOTALL, enable);
  }

  @JsonProperty
  public void setLiteral(final boolean enable) {
    flag(Pattern.LITERAL, enable);
  }

  @JsonProperty
  public void setMultiline(final boolean enable) {
    flag(Pattern.MULTILINE, enable);
  }

  @JsonProperty
  public void setUnicodeCase(final boolean enable) {
    flag(Pattern.UNICODE_CASE, enable);
  }

  @JsonProperty
  public void setUnixLines(final boolean enable) {
    flag(Pattern.UNIX_LINES, enable);
  }

  @VisibleForTesting
  boolean doMatch(final String subject) {
    if (compiled == null) {
      compiled = Pattern.compile(pattern, flags);
    }
    return compiled.matcher(subject).matches();
  }

  @Override
  public boolean matches(final String string) {
    return doMatch(string);
  }
}
