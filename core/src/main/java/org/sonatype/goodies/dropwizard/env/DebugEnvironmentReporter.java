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
package org.sonatype.goodies.dropwizard.env;

import org.slf4j.Logger;

/**
 * Debug {@link EnvironmentReporter}.
 *
 * @since ???
 */
public class DebugEnvironmentReporter
  extends DetailedEnvironmentReporter
{
  @Override
  public void report(final Logger logger) throws Exception {
    super.report(logger);

    boolean debug = logger.isDebugEnabled();
    if (debug) {
      logger.debug("Environment variables:");
      System.getenv().forEach((key, value) -> {
        logger.debug("  {}='{}'", key, value);
      });
    }

    if (debug) {
      logger.debug("System properties:");
      System.getProperties().forEach((key, value) -> {
        logger.debug("  {}='{}'", key, value);
      });
    }
  }
}
