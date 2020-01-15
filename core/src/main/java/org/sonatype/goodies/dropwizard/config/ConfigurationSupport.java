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
package org.sonatype.goodies.dropwizard.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.sonatype.goodies.dropwizard.health.HealthCheckConfiguration;
import org.sonatype.goodies.dropwizard.selection.ComponentSelectionConfiguration;
import org.sonatype.goodies.dropwizard.selection.ComponentSelectionConfigurationAware;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Application {@link Configuration} support.
 *
 * @since 1.2.0
 */
public class ConfigurationSupport
    extends Configuration
    implements ComponentSelectionConfigurationAware
{
  /**
   * Additional properties for Sisu injection.
   */
  @NotNull
  @Bind(name="configuration-properties")
  @JsonProperty("properties")
  private Map<String, Object> properties = new HashMap<>();

  @Nonnull
  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(@Nonnull final Map<String, Object> properties) {
    this.properties = checkNotNull(properties);
  }

  /**
   * Component selection configuration for Sisu component discovery.
   */
  @NotNull
  @Valid
  @Bind
  @JsonProperty("component-selection")
  private ComponentSelectionConfiguration componentSelectionConfiguration = new ComponentSelectionConfiguration();

  @Nonnull
  @Override
  public ComponentSelectionConfiguration getComponentSelectionConfiguration() {
    return componentSelectionConfiguration;
  }

  public void setComponentSelectionConfiguration(@Nonnull final ComponentSelectionConfiguration config) {
    this.componentSelectionConfiguration = checkNotNull(config);
  }

  /**
   * Standard health-check configuration.
   */
  @NotNull
  @Valid
  @Bind
  @JsonProperty("health-check")
  private HealthCheckConfiguration healthCheckConfiguration = new HealthCheckConfiguration();

  @Nonnull
  public HealthCheckConfiguration getHealthCheckConfiguration() {
    return healthCheckConfiguration;
  }

  public void setHealthCheckConfiguration(@Nonnull final HealthCheckConfiguration healthCheckConfiguration) {
    this.healthCheckConfiguration = checkNotNull(healthCheckConfiguration);
  }
}
