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
package org.sonatype.goodies.dropwizard.health;

import org.sonatype.goodies.dropwizard.util.Loggers;

import com.codahale.metrics.health.HealthCheck;
import org.slf4j.Logger;

/**
 * {@link HealthCheck} support.
 *
 * @since 1.3.0
 */
public abstract class HealthCheckSupport
    extends HealthCheck
{
  protected final Logger log = Loggers.getLogger(getClass());
}
