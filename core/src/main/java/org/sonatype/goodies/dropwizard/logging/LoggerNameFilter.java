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
package org.sonatype.goodies.dropwizard.logging;

import java.util.Set;

import javax.validation.constraints.NotEmpty;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.filter.FilterFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Filter events by logger-name.
 *
 * @since ???
 */
public class LoggerNameFilter
    extends Filter<ILoggingEvent>
{
  private final String[] include;

  public LoggerNameFilter(final Factory config) {
    checkNotNull(config);
    // convert set to array for optimal iteration
    this.include = config.getInclude().toArray(new String[0]);
  }

  @Override
  public FilterReply decide(final ILoggingEvent event) {
    String loggerName = event.getLoggerName();
    for (String prefix : include) {
      if (loggerName.startsWith(prefix)) {
        return FilterReply.ACCEPT;
      }
    }
    return FilterReply.DENY;
  }

  //
  // Factory
  //

  @JsonTypeName("logger-name")
  public static class Factory
      implements FilterFactory<ILoggingEvent>
  {
    @NotEmpty
    @JsonProperty
    private Set<String> include;

    public Set<String> getInclude() {
      return include;
    }

    public void setInclude(final Set<String> include) {
      this.include = checkNotNull(include);
    }

    @Override
    public Filter<ILoggingEvent> build() {
      return new LoggerNameFilter(this);
    }
  }
}
