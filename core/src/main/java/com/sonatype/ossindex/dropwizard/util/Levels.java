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

import ch.qos.logback.classic.Level;

/**
 * Logging level helpers.
 *
 * @since ???
 */
public final class Levels
{
  private Levels() {
    // empty
  }

  /**
   * Convert Logback level to JUL level.
   */
  public static java.util.logging.Level convert(final Level level) {
    switch (level.levelInt) {
      case Level.OFF_INT:
        return java.util.logging.Level.OFF;
      case Level.ERROR_INT:
        return java.util.logging.Level.SEVERE;
      case Level.WARN_INT:
        return java.util.logging.Level.WARNING;
      case Level.INFO_INT:
        return java.util.logging.Level.INFO;
      case Level.DEBUG_INT:
        return java.util.logging.Level.FINE;
      case Level.TRACE_INT:
        return java.util.logging.Level.FINEST;
      case Level.ALL_INT:
        return java.util.logging.Level.ALL;

      default:
        throw new IllegalArgumentException();
    }
  }
}
