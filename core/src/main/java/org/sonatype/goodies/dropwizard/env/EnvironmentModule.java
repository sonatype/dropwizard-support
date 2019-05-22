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
package org.sonatype.goodies.dropwizard.env;

import javax.validation.Validator;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.AdminEnvironment;
import io.dropwizard.setup.Environment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Adds various bindings for {@link Environment} properties.
 *
 * @since 1.0.0
 */
public class EnvironmentModule
    extends AbstractModule
{
  private final Environment environment;

  public EnvironmentModule(final Environment environment) {
    this.environment = checkNotNull(environment);
  }

  @Override
  protected void configure() {
    bind(Environment.class).toInstance(environment);
    bind(MetricRegistry.class).toInstance(environment.metrics());
    bind(HealthCheckRegistry.class).toInstance(environment.healthChecks());
    bind(ObjectMapper.class).toInstance(environment.getObjectMapper());
    bind(Validator.class).toInstance(environment.getValidator());
    bind(JerseyEnvironment.class).toInstance(environment.jersey());
    bind(ServletEnvironment.class).toInstance(environment.servlets());
    bind(LifecycleEnvironment.class).toInstance(environment.lifecycle());
    bind(AdminEnvironment.class).toInstance(environment.admin());
  }
}
