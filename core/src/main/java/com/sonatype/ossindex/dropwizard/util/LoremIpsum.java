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
package com.sonatype.ossindex.dropwizard.util;

// copied and modified from: https://github.com/sonatype/nexus-public/blob/master/components/nexus-common/src/main/java/org/sonatype/nexus/common/text/LoremIpsum.groovy

import static com.google.common.base.Preconditions.checkArgument;

/**
 * "Lorem Ipsum" placeholder-text helpers.
 *
 * @since ???
 */
public final class LoremIpsum
{
  private LoremIpsum() {
    // empty
  }

  private static final String TEXT =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna " +
      "aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
      "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint " +
      "occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

  private static final String[] WORDS = TEXT.split("\\s");

  public static String words(final int count) {
    checkArgument(count < WORDS.length);

    StringBuilder buff = new StringBuilder();
    for (int i = 0; i < count; i++) {
      buff.append(WORDS[i]);
      if (i + 1 < count) {
        buff.append(' ');
      }
    }

    return buff.toString();
  }

  public static String words(final int min, final int max) {
    int count = Rando.range(min, max);
    return words(count);
  }
}
