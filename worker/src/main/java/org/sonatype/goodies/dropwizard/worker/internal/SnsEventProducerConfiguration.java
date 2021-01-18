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

import javax.annotation.Nonnull;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link SnsEventProducerSupport} configuration.
 *
 * @since ???
 */
public class SnsEventProducerConfiguration
{
  /**
   * AWS SNS topic name or ARN.
   *
   * Using name will require permissions to list topics to resolve ARN.
   */
  @NotEmpty
  @JsonProperty
  private String topic;

  @Nonnull
  public String getTopic() {
    return topic;
  }

  public void setTopic(@Nonnull final String topic) {
    this.topic = checkNotNull(topic);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("topic", topic)
        .toString();
  }
}
