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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.goodies.dropwizard.util.FileHelper.resolvePath;

/**
 * Basic {@link EnvironmentReporter}.
 *
 * @since ???
 */
public class BasicEnvironmentReporter
  implements EnvironmentReporter
{
  @Override
  public void report(final Logger logger) throws Exception {
    checkNotNull(logger);

    logger.info("Java: {}, {}, {}, {}",
        System.getProperty("java.version"),
        System.getProperty("java.vm.name"),
        System.getProperty("java.vm.vendor"),
        System.getProperty("java.vm.version")
    );
    logger.info("OS: {}, {}, {}",
        System.getProperty("os.name"),
        System.getProperty("os.version"),
        System.getProperty("os.arch")
    );
    logger.info("User: {}, {}, {}",
        System.getProperty("user.name"),
        System.getProperty("user.language"),
        resolvePath(System.getProperty("user.home"))
    );

    logger.info("CWD: {}", resolvePath(System.getProperty("user.dir")));
    logger.info("TMP: {}", resolvePath(System.getProperty("java.io.tmpdir")));
  }
}
