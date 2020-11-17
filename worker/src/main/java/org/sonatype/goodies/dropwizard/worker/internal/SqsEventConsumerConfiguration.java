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
package org.sonatype.goodies.dropwizard.worker.internal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.dropwizard.util.Duration;
import io.dropwizard.validation.MinDuration;

/**
 * {@link SqsEventConsumerSupport} configuration.
 *
 * @since ???
 */
public class SqsEventConsumerConfiguration
{
  /**
   * AWS SQS queue name or ARN.
   *
   * Using name will require permissions to list queues to resolve ARN.
   */
  @NotEmpty
  @JsonProperty
  private String queue;

  @Min(1)
  @JsonProperty
  private int concurrentConsumers = 1;

  @Min(1)
  @JsonProperty
  private int maxMessagesPerPoll = 1;

  @NotNull
  @MinDuration(1 /*seconds*/)
  @JsonProperty
  private Duration waitTime = Duration.seconds(5);

  @NotNull
  @JsonProperty
  private Duration visibilityTimeout = Duration.minutes(2);

  public String getQueue() {
    return queue;
  }

  public void setQueue(final String queue) {
    this.queue = queue;
  }

  public int getConcurrentConsumers() {
    return concurrentConsumers;
  }

  public void setConcurrentConsumers(final int concurrentConsumers) {
    this.concurrentConsumers = concurrentConsumers;
  }

  public int getMaxMessagesPerPoll() {
    return maxMessagesPerPoll;
  }

  public void setMaxMessagesPerPoll(final int maxMessagesPerPoll) {
    this.maxMessagesPerPoll = maxMessagesPerPoll;
  }

  public Duration getWaitTime() {
    return waitTime;
  }

  public void setWaitTime(final Duration waitTime) {
    this.waitTime = waitTime;
  }

  public Duration getVisibilityTimeout() {
    return visibilityTimeout;
  }

  public void setVisibilityTimeout(final Duration visibilityTimeout) {
    this.visibilityTimeout = visibilityTimeout;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("queue", queue)
        .add("concurrentConsumers", concurrentConsumers)
        .add("maxMessagesPerPoll", maxMessagesPerPoll)
        .add("waitTime", waitTime)
        .add("visibilityTimeout", visibilityTimeout)
        .toString();
  }
}
