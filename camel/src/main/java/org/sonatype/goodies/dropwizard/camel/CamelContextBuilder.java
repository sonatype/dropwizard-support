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
package org.sonatype.goodies.dropwizard.camel;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.goodies.dropwizard.camel.metrics.ServiceMetricsRoutePolicyFactory;

import com.codahale.metrics.MetricRegistry;
import org.apache.camel.CamelContext;
import org.apache.camel.component.metrics.MetricsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.ExecutorServiceManager;
import org.apache.camel.spi.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * {@link CamelContext} builder.
 *
 * @since ???
 */
@Named
public class CamelContextBuilder
{
  private static final Logger log = LoggerFactory.getLogger(CamelContextBuilder.class);

  private final MetricRegistry metricRegistry;

  private String name;

  @Nullable
  private Map<String,Object> binding;

  @Inject
  public CamelContextBuilder(final MetricRegistry metricRegistry) {
    this.metricRegistry = checkNotNull(metricRegistry);
  }

  public CamelContextBuilder name(final String name) {
    this.name = checkNotNull(name);
    return this;
  }

  public CamelContextBuilder bind(final String id, final Object value) {
    checkNotNull(id);
    checkNotNull(value);
    if (binding == null) {
      binding = new HashMap<>();
    }
    binding.put(id, value);
    return this;
  }

  public CamelContextBuilder logger(final Logger logger) {
    bind("logger", logger);
    return this;
  }

  public DefaultCamelContext build() {
    checkState(name != null, "Missing: name");

    log.debug("Create camel-context: {}", name);
    DefaultCamelContext camelContext = new DefaultCamelContext();
    camelContext.setName(name);

    // install standard components into registry
    Registry registry = camelContext.getRegistry();
    if (binding != null) {
      binding.forEach(registry::bind);
    }
    registry.bind(MetricsComponent.METRIC_REGISTRY_NAME, metricRegistry);

    // adjust thread-name pattern, a bit less verbose than the defaults
    ExecutorServiceManager esm = camelContext.getExecutorServiceManager();
    esm.setThreadNamePattern("#camelId#: #name# ##counter#");

    // install adapter for route metrics
    camelContext.addRoutePolicyFactory(new ServiceMetricsRoutePolicyFactory());

    return camelContext;
  }
}
