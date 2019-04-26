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

import javax.inject.Named;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// copied from: https://github.com/sonatype/nexus-public/blob/master/components/nexus-base/src/main/java/org/sonatype/nexus/internal/metrics/MetricMediator.java

/**
 * Manages {@link Metric} registrations via component mediation.
 *
 * @since ???
 */
@Named
public class MetricMediator
    implements Mediator<Named, Metric, MetricRegistry>
{
  private static final Logger log = LoggerFactory.getLogger(MetricMediator.class);

  public void add(final BeanEntry<Named, Metric> entry, final MetricRegistry registry) throws Exception {
    log.debug("Registering: {}", entry);
    registry.register(entry.getKey().value(), entry.getValue());
  }

  public void remove(final BeanEntry<Named, Metric> entry, final MetricRegistry registry) throws Exception {
    log.debug("Un-registering: {}", entry);
    registry.remove(entry.getKey().value());
  }
}
