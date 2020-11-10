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
package org.sonatype.goodies.dropwizard.worker;

import javax.validation.Valid;

import org.sonatype.goodies.dropwizard.worker.internal.SnsEventProducerConfiguration;
import org.sonatype.goodies.dropwizard.worker.internal.SqsEventConsumerConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link WorkerService} configuration.
 *
 * @since ???
 */
public class WorkerServiceConfiguration
{
  @Valid
  @JsonProperty("producer")
  private SnsEventProducerConfiguration producerConfiguration;

  public SnsEventProducerConfiguration getProducerConfiguration() {
    return producerConfiguration;
  }

  public void setProducerConfiguration(final SnsEventProducerConfiguration producerConfiguration) {
    this.producerConfiguration = producerConfiguration;
  }

  @Valid
  @JsonProperty("consumer")
  private SqsEventConsumerConfiguration consumerConfiguration;

  public SqsEventConsumerConfiguration getConsumerConfiguration() {
    return consumerConfiguration;
  }

  public void setConsumerConfiguration(final SqsEventConsumerConfiguration consumerConfiguration) {
    this.consumerConfiguration = consumerConfiguration;
  }
}
