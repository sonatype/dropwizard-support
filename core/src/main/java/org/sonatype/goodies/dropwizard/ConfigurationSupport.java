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
{
  /**
   * Additional properties for Sisu injection.
   */
  @NotNull
  @JsonProperty("properties")
  private Map<String,Object> properties = new HashMap<>();

  /**
   * Optional set of component groups to enable.
   */
  @NotNull
  @JsonProperty("groups")
  private Set<String> groups = new HashSet<>();

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
  public Set<String> getGroups() {
    return groups;
  }

  public void setGroups(@Nonnull final Set<String> groups) {
    this.groups = checkNotNull(groups);
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
