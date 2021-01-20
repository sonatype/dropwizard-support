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
package org.sonatype.goodies.dropwizard.config;

import org.apache.commons.text.lookup.StringLookup;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * First-match {@link StringLookup}.
 *
 * @since 1.2.0
 */
public class FirstMatchStringLookup
    implements StringLookup
{
  private final StringLookup[] candidates;

  /**
   * When {@literal true} if unable to resolve non-null value for key will throw exception.
   */
  private final boolean strict;

  public FirstMatchStringLookup(final boolean strict, final StringLookup... candidates) {
    this.strict = strict;
    checkArgument(candidates.length > 0, "At least one candidate required");
    this.candidates = checkNotNull(candidates);
  }

  public FirstMatchStringLookup(final StringLookup... candidates) {
    this(true, candidates);
  }

  @Override
  public String lookup(final String key) {
    for (StringLookup candidate : candidates) {
      String value = candidate.lookup(key);
      if (value != null) {
        return value;
      }
    }
    if (strict) {
      throw new MissingSubstitutionValueException(key);
    }
    return null;
  }

  /**
   * @since ???
   */
  public static class MissingSubstitutionValueException
    extends RuntimeException
  {
    public MissingSubstitutionValueException(final String key) {
      super("Missing substitution value for key: " + key);
    }
  }
}
