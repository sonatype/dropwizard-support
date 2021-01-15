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
package org.sonatype.goodies.dropwizard.util;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

// copied from: https://github.com/sonatype/nexus-internal/blob/master/components/nexus-common/src/main/java/org/sonatype/nexus/common/text/Plural.java

/**
 * Helper to render plural strings.
 *
 * @since ???
 */
public final class Plural
{
  private Plural() {
    // empty
  }

  public static StringBuilder append(final StringBuilder buff,
                                     final int value,
                                     final String singular,
                                     @Nullable final String plural)
  {
    checkNotNull(buff);
    checkNotNull(singular);
    buff.append(value).append(" ");
    if (value == 1) {
      buff.append(singular);
    }
    else {
      if (plural == null) {
        buff.append(singular).append("s");
      }
      else {
        buff.append(plural);
      }
    }
    return buff;
  }

  public static StringBuilder append(final StringBuilder buff,
                                     final int value,
                                     final String singular)
  {
    return append(buff, value, singular, null);
  }


  public static String of(final int value, final String singular, @Nullable final String plural) {
    return append(new StringBuilder(), value, singular, plural).toString();
  }

  public static String of(final int value, final String singular) {
    return of(value, singular, null);
  }

  //public static String of(final Collection<?> collection, final String singular, @Nullable final String plural) {
  //  checkNotNull(collection);
  //  return of(collection.size(), singular, plural);
  //}
  //
  //public static String of(final Collection<?> collection, final String singular) {
  //  checkNotNull(collection);
  //  return of(collection.size(), singular, null);
  //}
}