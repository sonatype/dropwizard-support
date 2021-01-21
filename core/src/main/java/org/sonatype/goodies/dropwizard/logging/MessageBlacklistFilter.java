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

import java.util.function.BiPredicate;

import javax.validation.constraints.NotEmpty;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.filter.FilterFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.goodies.dropwizard.util.MoreStrings.blankToNull;

/**
 * Blacklist event based on event message with optional logger and level predicates.
 *
 * @since ???
 */
public class MessageBlacklistFilter
    extends Filter<ILoggingEvent>
{
  private final String logger;

  private final Level level;

  private final BiPredicate<String,String> function;

  private final String value;

  public MessageBlacklistFilter(final Factory config) {
    checkNotNull(config);
    this.logger = config.logger;
    this.level = config.level;
    this.function = checkNotNull(config.op.function);
    this.value = config.value;
  }

  @Override
  public FilterReply decide(final ILoggingEvent event) {
    boolean matched = true;
    if (logger != null && !event.getLoggerName().startsWith(logger)) {
      matched = false;
    }
    if (matched && level != null && !event.getLevel().isGreaterOrEqual(level)) {
      matched = false;
    }
    if (matched && !function.test(event.getMessage(), value)) {
      matched = false;
    }

    return matched ? FilterReply.DENY : FilterReply.NEUTRAL;
  }

  //
  // Factory
  //

  @SuppressWarnings("unused")
  public enum Operation {
    EQUALS(String::equals),
    STARTS_WITH(String::startsWith),
    ENDS_WITH(String::endsWith),
    CONTAINS(String::contains);

    final BiPredicate<String,String> function;

    Operation(final BiPredicate<String,String> function) {
      this.function = function;
    }
  }

  @JsonTypeName("blacklist-message")
  public static class Factory
      implements FilterFactory<ILoggingEvent>
  {
    @JsonProperty
    private String logger;

    public String getLogger() {
      return logger;
    }

    public void setLogger(final String logger) {
      this.logger = logger;
    }

    @JsonProperty
    private Level level;

    public Level getLevel() {
      return level;
    }

    public void setLevel(final Level level) {
      this.level = level;
    }

    @JsonProperty
    private Operation op;

    public Operation getOp() {
      return op;
    }

    public void setOp(final Operation op) {
      this.op = op;
    }

    @NotEmpty
    @JsonProperty
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(final String value) {
      //noinspection ConstantConditions
      this.value = checkNotNull(blankToNull(value));
    }

    @Override
    public Filter<ILoggingEvent> build() {
      return new MessageBlacklistFilter(this);
    }
  }
}
