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
package org.sonatype.goodies.dropwizard.events;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.dropwizard.util.Duration;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link EventService} configuration.
 *
 * @since 1.2.0
 */
public class EventConfiguration
{
  /**
   * {@link EventExecutor} configuration.
   */
  public static class EventExecutorConfiguration
  {
    @Min(0)
    @JsonProperty
    private int corePoolSize = 1;

    @Min(1)
    @JsonProperty
    private int maximumPoolSize = 10;

    @NotNull
    @Valid
    @JsonProperty
    private Duration keepAlive = Duration.seconds(60);

    @JsonProperty
    private boolean fairThreading;

    @NotNull
    @Valid
    @JsonProperty
    private Duration shutdownGracePeriod = Duration.seconds(5);

    public int getCorePoolSize() {
      return corePoolSize;
    }

    public void setCorePoolSize(final int corePoolSize) {
      this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
      return maximumPoolSize;
    }

    public void setMaximumPoolSize(final int maximumPoolSize) {
      this.maximumPoolSize = maximumPoolSize;
    }

    @NotNull
    public Duration getKeepAlive() {
      return keepAlive;
    }

    public void setKeepAlive(@NotNull final Duration keepAlive) {
      this.keepAlive = keepAlive;
    }

    public boolean isFairThreading() {
      return fairThreading;
    }

    public void setFairThreading(final boolean fairThreading) {
      this.fairThreading = fairThreading;
    }

    @NotNull
    public Duration getShutdownGracePeriod() {
      return shutdownGracePeriod;
    }

    public void setShutdownGracePeriod(@NotNull final Duration shutdownGracePeriod) {
      this.shutdownGracePeriod = shutdownGracePeriod;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("corePoolSize", corePoolSize)
          .add("maximumPoolSize", maximumPoolSize)
          .add("keepAlive", keepAlive)
          .add("fairThreading", fairThreading)
          .add("shutdownGracePeriod", shutdownGracePeriod)
          .toString();
    }
  }

  @NotNull
  @Valid
  @JsonProperty("executor")
  private EventExecutorConfiguration eventExecutorConfiguration = new EventExecutorConfiguration();

  @NotNull
  public EventExecutorConfiguration getEventExecutorConfiguration() {
    return eventExecutorConfiguration;
  }

  public void setEventExecutorConfiguration(@NotNull final EventExecutorConfiguration eventExecutorConfiguration) {
    this.eventExecutorConfiguration = checkNotNull(eventExecutorConfiguration);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("eventExecutorConfiguration", eventExecutorConfiguration)
        .toString();
  }
}
