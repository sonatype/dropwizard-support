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
package org.sonatype.goodies.dropwizard;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.sonatype.goodies.dropwizard.health.HealthCheckConfiguration;
import org.sonatype.goodies.dropwizard.selection.ComponentSelectionConfiguration;
import org.sonatype.goodies.dropwizard.selection.ComponentSelectionConfigurationAware;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;
import io.dropwizard.Configuration;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Application {@link Configuration} support.
 *
 * @since ???
 */
public class ConfigurationSupport
    extends Configuration
    implements ComponentSelectionConfigurationAware
{
  /**
   * Additional properties for Sisu injection.
   */
  @NotNull
  @JsonProperty("properties")
  private Map<String, Object> properties = new HashMap<>();

  /**
   * Component selection configuration for Sisu component discovery.
   */
  @NotNull
  @Valid
  @JsonProperty("component-selection")
  private ComponentSelectionConfiguration componentSelectionConfiguration = new ComponentSelectionConfiguration();

  /**
   * Standard health-check configuration.
   */
  @NotNull
  @Valid
  @Bind
  @JsonProperty("health-check")
  private HealthCheckConfiguration healthCheckConfiguration = new HealthCheckConfiguration();

  @Nonnull
  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(@NotNull final Map<String, Object> properties) {
    this.properties = checkNotNull(properties);
  }

  @Nonnull
  @Override
  public ComponentSelectionConfiguration getComponentSelectionConfiguration() {
    return componentSelectionConfiguration;
  }

  public void setComponentSelectionConfiguration(
      @Nonnull final ComponentSelectionConfiguration componentSelectionConfiguration)
  {
    this.componentSelectionConfiguration = checkNotNull(componentSelectionConfiguration);
  }

  @Nonnull
  public HealthCheckConfiguration getHealthCheckConfiguration() {
    return healthCheckConfiguration;
  }

  public void setHealthCheckConfiguration(@Nonnull final HealthCheckConfiguration healthCheckConfiguration) {
    this.healthCheckConfiguration = checkNotNull(healthCheckConfiguration);
  }

  // TODO: maybe pick a better name?

  @Documented
  @Retention(RUNTIME)
  @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
  @Beta
  public @interface Bind
  {
    Class<?> value() default Void.class;
  }
}
