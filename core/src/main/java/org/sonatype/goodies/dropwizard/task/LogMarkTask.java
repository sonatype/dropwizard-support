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
package org.sonatype.goodies.dropwizard.task;

import java.io.PrintWriter;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Strings;

/**
 * Mark the log.
 *
 * @since ???
 */
@Named
@Singleton
public class LogMarkTask
    extends TaskSupport
{
  public LogMarkTask() {
    super("log-mark");
  }

  @Override
  protected void doExecute(final Parameters parameters, final PrintWriter output) throws Exception {
    String message = parameters.value("message", "MARK");
    String asterixes = Strings.repeat("*", message.length() + 4);
    log.info("\n{}\n* {} *\n{}", asterixes, message, asterixes);
  }
}
