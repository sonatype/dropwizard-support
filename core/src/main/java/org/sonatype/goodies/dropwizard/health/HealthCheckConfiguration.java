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
package org.sonatype.goodies.dropwizard.health;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Health-check configuration.
 *
 * @since 1.2.0
 */
public class HealthCheckConfiguration
{
  @NotNull
  @Valid
  @JsonProperty
  private MemoryHealthCheck.Configuration memory = new MemoryHealthCheck.Configuration();

  @NotNull
  @Valid
  @JsonProperty("temporary-directory")
  private TemporaryDirectoryHealthCheck.Configuration temporaryDirectory = new TemporaryDirectoryHealthCheck.Configuration();

  public MemoryHealthCheck.Configuration getMemory() {
    return memory;
  }

  public void setMemory(final MemoryHealthCheck.Configuration memory) {
    this.memory = memory;
  }

  public TemporaryDirectoryHealthCheck.Configuration getTemporaryDirectory() {
    return temporaryDirectory;
  }

  public void setTemporaryDirectory(final TemporaryDirectoryHealthCheck.Configuration temporaryDirectory) {
    this.temporaryDirectory = temporaryDirectory;
  }
}
