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

import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.sonatype.goodies.dropwizard.camel.CamelContextBuilder;
import org.sonatype.goodies.dropwizard.worker.internal.SqsEventConsumerConfiguration;
import org.sonatype.goodies.dropwizard.worker.internal.SqsEventConsumerSupport;

import com.amazonaws.services.sqs.AmazonSQS;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link WorkEventEnvelope} consumer.
 *
 * @since ???
 */
public class WorkEventEnvelopeConsumer
    extends SqsEventConsumerSupport
{
  private final Consumer<WorkEventEnvelope> consumer;

  @Inject
  public WorkEventEnvelopeConsumer(final MetricRegistry metricRegistry,
                                   final Provider<CamelContextBuilder> camelContextBuilder,
                                   final AmazonSQS sqsClient,
                                   @Assisted final SqsEventConsumerConfiguration configuration,
                                   @Assisted final Consumer<WorkEventEnvelope> consumer)
  {
    super(
        metricRegistry,
        camelContextBuilder,
        sqsClient,
        configuration,
        WorkEventEnvelope.DATA_FORMAT,
        WorkEventEnvelope.SUBJECT_PREDICATE
    );
    this.consumer = checkNotNull(consumer);
  }

  @Override
  protected void consume(final Object envelope) {
    if (envelope instanceof WorkEventEnvelope) {
      consumer.accept((WorkEventEnvelope)envelope);
    }
    else {
      log.error("Unexpected: {}", envelope);
    }
  }

  //
  // Factory
  //

  public interface Factory
  {
    WorkEventEnvelopeConsumer create(SqsEventConsumerConfiguration configuration, Consumer<WorkEventEnvelope> consumer);
  }

  //
  // Module
  //

  @Named
  public static class FactoryModule
      implements Module
  {
    @Override
    public void configure(final Binder binder) {
      binder.install(new FactoryModuleBuilder()
          .build(Factory.class)
      );
    }
  }
}
