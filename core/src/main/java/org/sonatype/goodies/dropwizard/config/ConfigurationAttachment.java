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

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.inject.Module;
import io.dropwizard.jackson.Discoverable;

/**
 * Allows for generic configuration attachment to standard configuration framework.
 *
 * @since 1.2.0
 */
@JsonTypeInfo(use = Id.NAME, property = "type")
public interface ConfigurationAttachment
    extends Discoverable
{
  /**
   * Additional modules to install when attachment is configured.
   * 
   * @since ???
   */
  default List<Module> modules() {
    return Collections.emptyList();
  }
}
