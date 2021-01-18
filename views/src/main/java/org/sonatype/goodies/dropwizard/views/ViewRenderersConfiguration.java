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
package org.sonatype.goodies.dropwizard.views;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.Configuration;
import io.dropwizard.views.ViewConfigurable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * View renderers configuration.
 *
 * @since 1.2.0
 * @see ViewConfigurable#getViewConfiguration(Object)
 */
public class ViewRenderersConfiguration
{
  // SEE https://freemarker.apache.org/docs/api/freemarker/template/Configuration.html#setSetting-java.lang.String-java.lang.String-

  private final Map<String, Map<String, String>> renderers;

  @JsonCreator
  public ViewRenderersConfiguration(final Map<String, Map<String, String>> config) {
    checkNotNull(config);
    ImmutableMap.Builder<String, Map<String, String>> builder = ImmutableMap.builder();
    for (Map.Entry<String, Map<String, String>> entry : config.entrySet()) {
      builder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
    }
    this.renderers = builder.build();
  }

  public ViewRenderersConfiguration() {
    this(Collections.emptyMap());
  }

  public Map<String, Map<String, String>> asMap() {
    return renderers;
  }
}
