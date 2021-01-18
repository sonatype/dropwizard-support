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
import com.codahale.metrics.health.HealthCheck.Result;
import com.codahale.metrics.health.HealthCheck.ResultBuilder;
import org.apache.camel.health.HealthCheck.State;

/**
 * {@link HealthCheck} helpers.
 *
 * @since ???
 */
public class HealthCheckHelper2
{
  /**
   * Convert Apache Camel {@link org.apache.camel.health.HealthCheck.Result} to Dropwizard {@link HealthCheck.Result}.
   */
  public static HealthCheck.Result convert(final org.apache.camel.health.HealthCheck.Result source) {
    ResultBuilder builder = Result.builder();
    source.getDetails().forEach(builder::withDetail);
    source.getMessage().ifPresent(builder::withMessage);
    if (source.getState() == State.UP) {
      builder.healthy();
    }
    else {
      source.getError().ifPresentOrElse(builder::unhealthy, builder::unhealthy);
    }
    return builder.build();
  }
}
