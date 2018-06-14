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
package com.sonatype.ossindex.dropwizard.client;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

/**
 * Extended {@link JerseyClientConfiguration}.
 *
 * @see JerseyClientFactory
 * @since ???
 */
public class ExtJerseyClientConfiguration
    extends JerseyClientConfiguration
{
  @NotNull
  @Valid
  @JsonProperty("logging")
  private LoggingConfiguration loggingConfiguration = new LoggingConfiguration();

  public LoggingConfiguration getLoggingConfiguration() {
    return loggingConfiguration;
  }

  public void setLoggingConfiguration(LoggingConfiguration loggingConfiguration) {
    this.loggingConfiguration = loggingConfiguration;
  }
}
