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
package org.sonatype.goodies.dropwizard.config.attachment;

import java.util.List;

import org.sonatype.goodies.dropwizard.config.BindModuleSupport;
import org.sonatype.goodies.dropwizard.util.MoreStrings;
import org.sonatype.goodies.dropwizard.util.Plural;

import io.dropwizard.Configuration;

/**
 * Adds bindings for {@link ConfigurationAttachment}.
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
    // apply all attachments
    if (configuration instanceof ConfigurationAttachmentAware) {
      List<ConfigurationAttachment> attachments = ((ConfigurationAttachmentAware)configuration).getConfigurationAttachments();
      if (log.isDebugEnabled()) {
        log.debug("Applying {}", Plural.of(attachments, "attachment"));
      }
      attachments.forEach(this::apply);
    }
  }

  private void apply(final ConfigurationAttachment attachment) {
    Class<?> type = attachment.getClass();
    String name = attachment.name();
    if (log.isDebugEnabled()) {
      String typeName = type.getCanonicalName();
      log.debug("Attachment: {} named {} -> {}",
          typeName,
          MoreStrings.dquote(name),
          attachment
      );
    }

    // install any additional modules
    attachment.modules().forEach(this::install);

    // bind attachment and expose any nested bindings
    bind(type, name, attachment);
    expose(attachment);
  }
}
