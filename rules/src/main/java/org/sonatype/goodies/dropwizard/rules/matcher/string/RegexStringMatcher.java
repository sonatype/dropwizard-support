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

  private boolean invert;

  @JsonCreator
  public RegexStringMatcher(@NotNull @JsonProperty("pattern") final String pattern) {
    this.pattern = checkNotNull(pattern);
  }

  public String getPattern() {
    return pattern;
  }

  @Nullable
  public Pattern getCompiled() {
    return compiled;
  }

  public int getFlags() {
    return flags;
  }

  public boolean isInvert() {
    return invert;
  }

  private void flag(final int flag, final boolean enable) {
    if (enable) {
      flags = flags | flag;
    }
  }

  /**
   * Enable {@link Pattern#CANON_EQ} flag.
   */
  @JsonProperty
  public void setCanonEq(final boolean enable) {
    flag(Pattern.CANON_EQ, enable);
  }

  /**
   * Enable {@link Pattern#CASE_INSENSITIVE} flag.
   */
  @JsonProperty
  public void setIgnoreCase(final boolean enable) {
    flag(Pattern.CASE_INSENSITIVE, enable);
  }

  /**
   * Enable {@link Pattern#COMMENTS} flag.
   */
  @JsonProperty
  public void setComments(final boolean enable) {
    flag(Pattern.COMMENTS, enable);
  }

  /**
   * Enable {@link Pattern#DOTALL} flag.
   */
  @JsonProperty
  public void setDotAll(final boolean enable) {
    flag(Pattern.DOTALL, enable);
  }

  /**
   * Enable {@link Pattern#LITERAL} flag.
   */
  @JsonProperty
  public void setLiteral(final boolean enable) {
    flag(Pattern.LITERAL, enable);
  }

  /**
   * Enable {@link Pattern#MULTILINE} flag.
   */
  @JsonProperty
  public void setMultiline(final boolean enable) {
    flag(Pattern.MULTILINE, enable);
  }

  /**
   * Enable {@link Pattern#UNICODE_CASE} flag.
   */
  @JsonProperty
  public void setUnicodeCase(final boolean enable) {
    flag(Pattern.UNICODE_CASE, enable);
  }

  /**
   * Enable {@link Pattern#UNIX_LINES} flag.
   */
  @JsonProperty
  public void setUnixLines(final boolean enable) {
    flag(Pattern.UNIX_LINES, enable);
  }

  /**
   * Enable inverted/negative match.
   *
   * When set; matcher will invert the results of the regular-expression.
   */
  @JsonProperty
  public void setInvert(final boolean invert) {
    this.invert = invert;
  }

  @VisibleForTesting
  boolean doMatch(final String subject) {
    if (compiled == null) {
      compiled = Pattern.compile(pattern, flags);
    }
    boolean match = compiled.matcher(subject).matches();
    return invert != match;
  }

  @Override
  public boolean matches(@Nullable final String string) {
    if (string == null) {
      return false;
    }
    return doMatch(string);
  }
}
