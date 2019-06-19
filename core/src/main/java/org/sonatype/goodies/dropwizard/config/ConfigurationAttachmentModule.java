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

import java.util.Map;

import io.dropwizard.Configuration;

/**
 * Adds bindings for {@link ConfigurationAttachment configuration-attachements}.
 *
 * @since ???
 */
public class ConfigurationAttachmentModule
    extends BindModuleSupport
{
  public ConfigurationAttachmentModule(final Configuration configuration) {
    super(configuration);
  }

  @Override
  protected void configure() {
    // bind named attachments; and expose any bindings
    if (configuration instanceof ConfigurationAttachmentAware) {
      Map<String, ConfigurationAttachment> attachments = ((ConfigurationAttachmentAware)configuration).getConfigurationAttachments();
      log.debug("{} attachments", attachments.size());
      for (Map.Entry<String,ConfigurationAttachment> entry : attachments.entrySet()) {
        String name = entry.getKey();
        ConfigurationAttachment value = entry.getValue();
        log.debug("Attachment: {} -> {}", name, value);
        bind(value.getClass(), name, value);
        expose(value);
      }
    }
  }
}
