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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging {@link ApplicationCustomizer}.
 *
 * @since 1.3.0
 */
public class LoggingCustomizer
    implements ApplicationCustomizer
{
  private static final Logger log = LoggerFactory.getLogger(LoggingCustomizer.class);

  @Override
  public void customize(final ApplicationSupport application, final Configuration config, final Environment environment)
      throws Exception
  {
    if (config instanceof LoggingConfigurationAware) {
      LoggingConfiguration lconfig = ((LoggingConfigurationAware)config).getLoggingConfiguration();
      if (lconfig != null && lconfig.isEnabled()) {
        environment.jersey().register(new LoggingFeature(lconfig));
        log.info("Enabled");
      }
    }
  }
}
