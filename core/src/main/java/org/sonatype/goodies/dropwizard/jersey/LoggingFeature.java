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
package org.sonatype.goodies.dropwizard.jersey;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.sonatype.goodies.dropwizard.common.logging.Levels;

import ch.qos.logback.classic.Level;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Logging feature.
 *
 * Wrapper around {@link org.glassfish.jersey.logging.LoggingFeature} to limit configuration types exposed.
 *
 * @since 1.0.2
 */
public class LoggingFeature
    implements Feature
{
  private String logger = getClass().getName();

  private Level level = Level.TRACE;

  private Verbosity verbosity = Verbosity.PAYLOAD_ANY;

  private int maxEntitySize = 1024 * 8;

  public LoggingFeature(final String logger, final Level level, final Verbosity verbosity, final int maxEntitySize) {
    this.logger = checkNotNull(logger);
    this.level = checkNotNull(level);
    this.verbosity = checkNotNull(verbosity);
    this.maxEntitySize = maxEntitySize;
  }

  public LoggingFeature(final LoggingConfiguration config) {
    this(config.getLogger(), config.getLevel(), config.getVerbosity(), config.getMaxEntitySize());
  }

  public LoggingFeature() {
    // empty
  }

  public String getLogger() {
    return logger;
  }

  public void setLogger(final String logger) {
    this.logger = logger;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(final Level level) {
    this.level = level;
  }

  public Verbosity getVerbosity() {
    return verbosity;
  }

  public void setVerbosity(final Verbosity verbosity) {
    this.verbosity = verbosity;
  }

  public int getMaxEntitySize() {
    return maxEntitySize;
  }

  public void setMaxEntitySize(final int maxEntitySize) {
    this.maxEntitySize = maxEntitySize;
  }

  @Override
  public boolean configure(final FeatureContext context) {
    checkState(logger != null);
    checkState(level != null);
    checkState(verbosity != null);
    checkState(maxEntitySize > 0);

    return new org.glassfish.jersey.logging.LoggingFeature(
        java.util.logging.Logger.getLogger(logger),
        Levels.convert(level),
        verbosity,
        maxEntitySize
    ).configure(context);
  }
}
