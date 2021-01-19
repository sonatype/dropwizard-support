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
package org.sonatype.goodies.dropwizard.config;

import org.sonatype.goodies.dropwizard.config.attachment.ConfigurationAttachmentModule;

import io.dropwizard.Configuration;

/**
 * Adds bindings for {@link Configuration}.
 *
 * @since 1.0.0
 */
public class ConfigurationModule
    extends BindModuleSupport
{
  public ConfigurationModule(final Configuration configuration) {
    super(configuration);
  }

  @Override
  protected void configure() {
    // bind original configuration
    bind(configuration.getClass(), null, configuration);

    // expose configuration member bindings
    expose(configuration);

    // support configuration attachments
    install(new ConfigurationAttachmentModule(configuration));
  }
}
