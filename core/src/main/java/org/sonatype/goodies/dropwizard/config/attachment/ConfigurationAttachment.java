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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.sonatype.goodies.dropwizard.config.ConfigurationSupport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.inject.Module;
import io.dropwizard.jackson.Discoverable;

/**
 * Allows for dynamic configuration to be attached to primary {@link ConfigurationSupport}.
 *
 * @since ???
 * @see ConfigurationAttachmentAware
 * @see ConfigurationAttachmentModule
 * @see ConfigurationAttachmentSupport
 */
@JsonTypeInfo(use = Id.NAME, property = "type")
public interface ConfigurationAttachment
    extends Discoverable
{
  @Nullable
  default String name() {
    return null;
  }

  /**
   * Additional modules to install when attachment is configured.
   * 
   * @since ???
   */
  default List<Module> modules() {
    return Collections.emptyList();
  }
}
