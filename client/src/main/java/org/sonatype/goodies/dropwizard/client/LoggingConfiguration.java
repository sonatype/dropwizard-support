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
package org.sonatype.goodies.dropwizard.client;

import javax.validation.constraints.NotNull;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.glassfish.jersey.logging.LoggingFeature;

/**
 * {@link LoggingFeature} Configuration.
 *
 * @since 1.0.0
 */
public class LoggingConfiguration
{
  @JsonProperty
  private boolean enabled = false;

  @NotNull
  @JsonProperty
  private String logger = LoggingFeature.DEFAULT_LOGGER_NAME;

  @NotNull
  @JsonProperty
  private Level level = Level.TRACE;

  @NotNull
  @JsonProperty
  private LoggingFeature.Verbosity verbosity = LoggingFeature.Verbosity.PAYLOAD_ANY;

  @JsonProperty
  private int maxEntitySize = LoggingFeature.DEFAULT_MAX_ENTITY_SIZE;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getLogger() {
    return logger;
  }

  public void setLogger(String logger) {
    this.logger = logger;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public LoggingFeature.Verbosity getVerbosity() {
    return verbosity;
  }

  public void setVerbosity(LoggingFeature.Verbosity verbosity) {
    this.verbosity = verbosity;
  }

  public int getMaxEntitySize() {
    return maxEntitySize;
  }

  public void setMaxEntitySize(int maxEntitySize) {
    this.maxEntitySize = maxEntitySize;
  }
}
