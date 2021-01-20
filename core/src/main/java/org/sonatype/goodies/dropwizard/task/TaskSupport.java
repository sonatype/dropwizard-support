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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.sonatype.goodies.dropwizard.common.logging.Loggers;

import io.dropwizard.servlets.tasks.Task;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Support for {@link Task} implementations.
 *
 * @since 1.0.0
 */
public abstract class TaskSupport
    extends Task
{
  protected final Logger log = Loggers.getLogger(getClass());

  protected TaskSupport(final String name) {
    super(name);
  }

  /**
   * Helper to access task parameters.
   */
  public static class Parameters
  {
    private final Map<String, List<String>> parameters;

    public Parameters(final Map<String, List<String>> parameters) {
      this.parameters = checkNotNull(parameters);
    }

    @Nullable
    public String value(final String name) {
      checkNotNull(name);
      Collection<String> values = parameters.get(name);
      if (!values.isEmpty()) {
        return values.iterator().next();
      }
      return null;
    }

    public String value(final String name, final String defaultValue) {
      checkNotNull(defaultValue);
      String value = value(name);
      if (value != null) {
        return value;
      }
      return defaultValue;
    }

    public String require(final String name) {
      String value = value(name);
      checkState(value != null, "Missing parameter: %s", name);
      return value;
    }
  }

  @Override
  public void execute(final Map<String, List<String>> parameters, final PrintWriter output) throws Exception {
    doExecute(new Parameters(parameters), output);
  }

  protected abstract void doExecute(final Parameters parameters, final PrintWriter output) throws Exception;
}
