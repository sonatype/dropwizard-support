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
package org.sonatype.goodies.dropwizard.jersey;

import org.sonatype.goodies.dropwizard.app.ApplicationCustomizer;
import org.sonatype.goodies.dropwizard.app.ApplicationSupport;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Logging {@link ApplicationCustomizer}.
 *
 * @since ???
 */
public class LoggingCustomizer
    implements ApplicationCustomizer
{
  private final LoggingConfiguration configuration;

  public LoggingCustomizer(final LoggingConfiguration configuration) {
    this.configuration = checkNotNull(configuration);
  }

  @Override
  public void customize(final ApplicationSupport application, final Configuration config, final Environment environment)
      throws Exception
  {
    if (configuration.isEnabled()) {
      environment.jersey().register(new LoggingFeature(configuration));
    }
  }
}
