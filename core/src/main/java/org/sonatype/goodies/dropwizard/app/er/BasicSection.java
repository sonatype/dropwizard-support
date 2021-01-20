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
package org.sonatype.goodies.dropwizard.app.er;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;

import static org.sonatype.goodies.dropwizard.util.FileHelper.resolvePath;

/**
 * Basic {@link EnvironmentReport.Section}.
 *
 * @since ???
 */
@JsonTypeName(BasicSection.TYPE)
public class BasicSection
  extends EnvironmentReport.Section
{
  public static final String TYPE = "basic";

  public enum Include {
    JAVA, OS, USER, PATH
  }

  @JsonProperty
  private Set<Include> includes = ImmutableSet.copyOf(Include.values());

  public BasicSection() {
    super(TYPE);
  }

  @Override
  public void report(final Logger logger) throws Exception {
    logger.trace("Includes: {}", includes);

    if (includes.contains(Include.JAVA)) {
      log(logger, "Java: {}, {}, {}, {}",
          System.getProperty("java.version"),
          System.getProperty("java.vm.name"),
          System.getProperty("java.vm.vendor"),
          System.getProperty("java.vm.version")
      );
    }
    if (includes.contains(Include.OS)) {
      log(logger, "OS: {}, {}, {}",
          System.getProperty("os.name"),
          System.getProperty("os.version"),
          System.getProperty("os.arch")
      );
    }
    if (includes.contains(Include.USER)) {
      log(logger, "User: {}, {}, {}",
          System.getProperty("user.name"),
          System.getProperty("user.language"),
          resolvePath(System.getProperty("user.home"))
      );
    }
    if (includes.contains(Include.PATH)) {
      log(logger, "CWD: {}", resolvePath(System.getProperty("user.dir")));
      log(logger, "TMP: {}", resolvePath(System.getProperty("java.io.tmpdir")));
    }
  }
}
