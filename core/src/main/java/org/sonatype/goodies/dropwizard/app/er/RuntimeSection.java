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

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.slf4j.Logger;

/**
 * Runtime {@link EnvironmentReport.Section}.
 *
 * @since ???
 */
@JsonTypeName(RuntimeSection.TYPE)
public class RuntimeSection
  extends EnvironmentReport.Section
{
  public static final String TYPE = "runtime";

  public RuntimeSection() {
    super(TYPE);
  }

  @Override
  public void report(final Logger logger) throws Exception {
    Runtime runtime = Runtime.getRuntime();
    log(logger,"CPU; processors={}", runtime.availableProcessors());
    log(logger,"Memory; free={}, total={}, max={}",
        runtime.freeMemory(),
        runtime.totalMemory(),
        runtime.maxMemory()
    );
  }
}
