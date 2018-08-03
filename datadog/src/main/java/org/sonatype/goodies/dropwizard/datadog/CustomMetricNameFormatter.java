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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Custom Datadog {@link MetricNameFormatter}.
 *
 * @since 1.0.0
 */
public class CustomMetricNameFormatter
    extends DefaultMetricNameFormatter
{
  private static final Logger log = LoggerFactory.getLogger(CustomMetricNameFormatter.class);

  /**
   * Default replacements for common/standard Dropwizard components that produce metrics.
   */
  private static final LinkedHashMap<String,String> DEFAULT_REPLACEMENTS;

  static {
    LinkedHashMap<String,String> replacements = new LinkedHashMap<>();
    replacements.put("io.dropwizard.jetty.MutableServletContextHandler", "dropwizard.servlet");
    replacements.put("org.eclipse.jetty.util.thread.QueuedThreadPool", "jetty.qtp");
    replacements.put("org.eclipse.jetty.server.HttpConnectionFactory", "jetty.connection-factory");
    replacements.put("ch.qos.logback.core.Appender", "logback.appender");
    replacements.put("org.apache.http.conn.HttpClientConnectionManager", "httpclient.connection-manager");
    replacements.put("org.apache.http.client.HttpClient", "httpclient");
    // fallbacks
    replacements.put("io.dropwizard", "dropwizard");
    replacements.put("org.eclipse.jetty", "jetty");
    replacements.put("ch.qos.logback.core", "logback");
    replacements.put("org.apache.http", "httpclient");
    DEFAULT_REPLACEMENTS = replacements;
  }

  private final Factory config;

  public CustomMetricNameFormatter(final Factory config) {
    this.config = checkNotNull(config);
    log.debug("Prefix: {}", config.prefix);
    log.debug("Default replacements: {}", config.defaultReplacements);
    log.debug("Replacements: {}", config.replacements);
  }

  private static void replace(final StringBuilder buff, final String from, final String to) {
    int i = buff.indexOf(from);
    if (i != -1) {
      buff.replace(i, i + from.length(), to);
    }
  }

  @Override
  public String format(final String name, final String... path) {
    StringBuilder buff = new StringBuilder(name);

    if (config.defaultReplacements) {
      for (Map.Entry<String,String> entry : DEFAULT_REPLACEMENTS.entrySet()) {
        replace(buff, entry.getKey(), entry.getValue());
      }
    }

    if (config.replacements != null) {
      for (Map.Entry<String,String> entry : config.replacements.entrySet()) {
        replace(buff, entry.getKey(), entry.getValue());
      }
    }

    if (config.prefix != null) {
      buff.insert(0, config.prefix);
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

    /**
     * @since 1.0.1
     */
    @JsonProperty
    private boolean defaultReplacements = true;

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

    /**
     * @since 1.0.1
     */
    public boolean isDefaultReplacements() {
      return defaultReplacements;
    }

    /**
     * @since 1.0.1
     */
    public void setDefaultReplacements(final boolean defaultReplacements) {
      this.defaultReplacements = defaultReplacements;
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
      return new CustomMetricNameFormatter(this);
    }
  }
}
