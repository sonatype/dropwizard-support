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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * View configuration.
 *
 * @since 1.3.0
 */
public class ViewConfiguration
{
  @NotNull
  private Map<String, Map<String, String>> renderersConfiguration = Collections.emptyMap();

  @JsonProperty("renderers")
  public Map<String, Map<String, String>> getRenderersConfiguration() {
    return renderersConfiguration;
  }

  @JsonProperty("renderers")
  public void setRenderersConfiguration(@NotNull final Map<String, Map<String, String>> config) {
    checkNotNull(config);
    ImmutableMap.Builder<String, Map<String, String>> builder = ImmutableMap.builder();
    for (Map.Entry<String, Map<String, String>> entry : config.entrySet()) {
      builder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
    }
    this.renderersConfiguration = builder.build();
  }
}
