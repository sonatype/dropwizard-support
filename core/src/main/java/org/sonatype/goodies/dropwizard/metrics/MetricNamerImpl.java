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
package org.sonatype.goodies.dropwizard.metrics;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.palominolabs.metrics.guice.MetricNamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Customized metric naming.
 *
 * @since 1.0.1
 * @see MetricNameFormat
 */
@SuppressWarnings("Duplicates")
public class MetricNamerImpl
    implements MetricNamer
{
  private static final Logger log = LoggerFactory.getLogger(MetricNamerImpl.class);

  /**
   * Generate metric name.
   */
  private String metricName(final Method method, final String name) {
    String result;
    Class<?> type = method.getDeclaringClass();

    // apply customized formatting if annotation is present
    MetricNameFormat format = type.getAnnotation(MetricNameFormat.class);
    if (format != null) {
      result = format.value()
          .replace("#class", type.getName())
          .replace("#simpleClass", type.getSimpleName())
          .replace("#method", method.getName())
          .replace("#name", name.isEmpty() ? method.getName() : name);
    }
    else if (name.isEmpty()) {
      result = name(type.getName(), method.getName());
    }
    else {
      result = name(type.getName(), name);
    }

    if (log.isTraceEnabled()) {
      log.trace("Metric-name: {}:{}:{} -> {}", type.getName(), method.getName(), name, result);
    }

    return result;
  }

  @Nonnull
  @Override
  public String getNameForCounted(@Nonnull final Method method, @Nonnull final Counted counted) {
    if (counted.absolute()) {
      return counted.name();
    }
    else {
      return metricName(method, counted.name());
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
    else {
      return metricName(method, exceptionMetered.name());
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
    else {
      return metricName(method, gauge.name());
    }
  }

  @Nonnull
  @Override
  public String getNameForMetered(@Nonnull final Method method, @Nonnull final Metered metered) {
    if (metered.absolute()) {
      return metered.name();
    }
    else {
      return metricName(method, metered.name());
    }
  }

  @Nonnull
  @Override
  public String getNameForTimed(@Nonnull final Method method, @Nonnull final Timed timed) {
    if (timed.absolute()) {
      return timed.name();
    }
    else {
      return metricName(method, timed.name());
    }
  }
}
