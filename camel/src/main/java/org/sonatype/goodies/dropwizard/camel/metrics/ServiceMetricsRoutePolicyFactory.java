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
package org.sonatype.goodies.dropwizard.camel.metrics;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicy;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;

/**
 * Service {@link MetricsRoutePolicyFactory}.
 *
 * Sets up the default {@link MetricsRoutePolicyFactory#getNamePattern() name-pattern} for service usage.
 *
 * @since 1.3.0
 */
public class ServiceMetricsRoutePolicyFactory
    extends MetricsRoutePolicyFactory
{
  public ServiceMetricsRoutePolicyFactory() {
    setNamePattern(String.format("service.%s.route.%s.%s",
        MetricsRoutePolicy.NAME_TOKEN,
        MetricsRoutePolicy.ROUTE_ID_TOKEN,
        MetricsRoutePolicy.TYPE_TOKEN
    ));
  }
}
