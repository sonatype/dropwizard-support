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
package org.sonatype.goodies.dropwizard.internal;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.AbstractModule;
import com.palominolabs.metrics.guice.MetricNamer;
import com.palominolabs.metrics.guice.MetricsInstrumentationModule;
import io.dropwizard.setup.Environment;

import static com.codahale.metrics.MetricRegistry.name;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Adds support for Guice-AOP based metrics.
 *
 * @since 1.0.0
 */
public class MetricsAopModule
    extends AbstractModule
{
  private final Environment environment;

  public MetricsAopModule(final Environment environment) {
    this.environment = checkNotNull(environment);
  }

  @Override
  protected void configure() {
    install(MetricsInstrumentationModule.builder()
        .withMetricRegistry(environment.metrics())
        .withMetricNamer(new MetricNamerImpl())
        .build());
  }

  /**
   * Customized metric naming; default appends suffixes which are non-standard with other DW metrics.
   */
  @SuppressWarnings("Duplicates")
  private static class MetricNamerImpl
      implements MetricNamer
  {
    @Nonnull
    @Override
    public String getNameForCounted(@Nonnull final Method method, @Nonnull final Counted counted) {
      if (counted.absolute()) {
        return counted.name();
      }
      else if (counted.name().isEmpty()) {
        return name(method.getDeclaringClass(), method.getName());
      }
      else {
        return name(method.getDeclaringClass(), counted.name());
      }
    }

    @Nonnull
    @Override
    public String getNameForExceptionMetered(@Nonnull final Method method,
                                             @Nonnull final ExceptionMetered exceptionMetered)
    {
      if (exceptionMetered.absolute()) {
        return exceptionMetered.name();
      }
      else if (exceptionMetered.name().isEmpty()) {
        return
            name(method.getDeclaringClass(), method.getName());
      }
      else {
        return name(method.getDeclaringClass(), exceptionMetered.name());
      }
    }

    @Nonnull
    @Override
    public String getNameForGauge(@Nonnull final Class<?> instanceClass,
                                  @Nonnull final Method method,
                                  @Nonnull final Gauge gauge)
    {
      if (gauge.absolute()) {
        return gauge.name();
      }
      else if (gauge.name().isEmpty()) {
        return name(method.getDeclaringClass(), method.getName());
      }
      else {
        return name(method.getDeclaringClass(), gauge.name());
      }
    }

    @Nonnull
    @Override
    public String getNameForMetered(@Nonnull final Method method, @Nonnull final Metered metered) {
      if (metered.absolute()) {
        return metered.name();
      }
      else if (metered.name().isEmpty()) {
        return name(method.getDeclaringClass(), method.getName());
      }
      else {
        return name(method.getDeclaringClass(), metered.name());
      }
    }

    @Nonnull
    @Override
    public String getNameForTimed(@Nonnull final Method method, @Nonnull final Timed timed) {
      if (timed.absolute()) {
        return timed.name();
      }
      else if (timed.name().isEmpty()) {
        return name(method.getDeclaringClass(), method.getName());
      }
      else {
        return name(method.getDeclaringClass(), timed.name());
      }
    }
  }
}
