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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.camel.metrics.ServiceMetricsRoutePolicyFactory;

import com.codahale.metrics.MetricRegistry;
import org.apache.camel.component.metrics.MetricsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.ExecutorServiceManager;
import org.apache.camel.spi.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link org.apache.camel.CamelContext} factory.
 *
 * @since ???
 */
@Named
@Singleton
public class CamelContextFactory
{
  private static final Logger log = LoggerFactory.getLogger(CamelContextFactory.class);

  private final MetricRegistry metricRegistry;

  @Inject
  public CamelContextFactory(final MetricRegistry metricRegistry) {
    this.metricRegistry = checkNotNull(metricRegistry);
  }

  public DefaultCamelContext create(final String name) {
    log.debug("Create camel-context: {}", name);
    DefaultCamelContext camelContext = new DefaultCamelContext();
    camelContext.setName(name);

    // install standard components into registry
    Registry registry = camelContext.getRegistry();
    registry.bind(MetricsComponent.METRIC_REGISTRY_NAME, metricRegistry);

    // adjust thread-name pattern, a bit less verbose than the defaults
    ExecutorServiceManager esm = camelContext.getExecutorServiceManager();
    esm.setThreadNamePattern("#camelId#: #name# ##counter#");

    // install adapter for route metrics
    camelContext.addRoutePolicyFactory(new ServiceMetricsRoutePolicyFactory());

    return camelContext;
  }
}
