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
package org.sonatype.goodies.dropwizard.datadog;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.coursera.metrics.datadog.DefaultMetricNameFormatter;
import org.coursera.metrics.datadog.MetricNameFormatter;
import org.coursera.metrics.datadog.MetricNameFormatterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Datadog metric-name formatter.
 *
 * @since 1.0.0
 */
public class MetricNameFormatterImpl
    extends DefaultMetricNameFormatter
{
  private static final Logger log = LoggerFactory.getLogger(MetricNameFormatterImpl.class);

  @Nullable
  private final String prefix;

  @Nullable
  private final LinkedHashMap<String,String> replacements;

  public MetricNameFormatterImpl(@Nullable final String prefix, @Nullable final LinkedHashMap<String,String> replacements) {
    this.prefix = prefix;
    log.debug("Prefix: {}", prefix);

    this.replacements = replacements;
    log.debug("Replacements: {}", replacements);
  }

  @Override
  public String format(final String name, final String... path) {
    StringBuilder buff = new StringBuilder(name);

    if (replacements != null) {
      for (Map.Entry<String,String> entry : replacements.entrySet()) {
        String from = entry.getKey();
        int i = buff.indexOf(from);
        if (i != -1) {
          String to = entry.getValue();
          buff.replace(i, i + from.length(), to);
        }
      }
    }

    if (prefix != null) {
      buff.insert(0, prefix);
    }

    String result = super.format(buff.toString(), path);

    if (log.isTraceEnabled()) {
      log.trace("Format; name={}, path={} -> {}", name, Arrays.asList(path), result);
    }

    return result;
  }

  //
  // Factory
  //

  @JsonTypeName("custom")
  public static class Factory
      implements MetricNameFormatterFactory
  {
    @Nullable
    @JsonProperty
    private String prefix;

    @Nullable
    @JsonProperty
    private LinkedHashMap<String,String> replacements;

    @Nullable
    public String getPrefix() {
      return prefix;
    }

    public void setPrefix(@Nullable final String prefix) {
      this.prefix = prefix;
    }

    @Nullable
    public LinkedHashMap<String, String> getReplacements() {
      return replacements;
    }

    public void setReplacements(@Nullable final LinkedHashMap<String, String> replacements) {
      this.replacements = replacements;
    }

    @Override
    public MetricNameFormatter build() {
      return new MetricNameFormatterImpl(prefix, replacements);
    }
  }
}
