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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Match if string starts with another.
 *
 * @since ???
 */
@JsonTypeName(PrefixStringMatcher.NAME)
public class PrefixStringMatcher
    extends StringMatcherSupport
{
  public static final String NAME = "prefix";

  @JsonCreator
  public PrefixStringMatcher(@NotNull @JsonProperty("value") final String value,
                             @JsonProperty("ignoreCase") final boolean ignoreCase)
  {
    super(NAME, value, ignoreCase);
  }

  @Override
  protected boolean doMatch(final String subject) {
    return subject.startsWith(value);
  }
}
