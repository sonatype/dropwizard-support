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
package org.sonatype.goodies.dropwizard.camel.health;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.health.ContextHealthCheck;
import org.apache.camel.support.LifecycleStrategySupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages lifecycle of {@link CamelContext} health-check.
 *
 * @since ???
 */
public class ContextHealthCheckLifecycleStrategy
    extends LifecycleStrategySupport
{
  private final HealthCheckRegistry healthCheckRegistry;

  private final CamelContext camelContext;

  private final String name;

  public ContextHealthCheckLifecycleStrategy(final HealthCheckRegistry healthCheckRegistry,
                                             final CamelContext camelContext)
  {
    this.healthCheckRegistry = checkNotNull(healthCheckRegistry);
    this.camelContext = checkNotNull(camelContext);
    this.name = String.format("%s-context", camelContext.getName());
  }

  @Override
  public void onContextStarted(final CamelContext context) {
    final ContextHealthCheck delegate = new ContextHealthCheck();
    delegate.setCamelContext(camelContext);

    healthCheckRegistry.register(name, new HealthCheck()
    {
      @Override
      protected Result check() throws Exception {
        return HealthCheckHelper2.convert(delegate.call());
      }
    });
  }

  @Override
  public void onContextStopping(final CamelContext context) {
    healthCheckRegistry.unregister(name);
  }
}
