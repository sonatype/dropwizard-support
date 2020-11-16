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

import javax.inject.Named;
import javax.inject.Provider;

import org.sonatype.goodies.dropwizard.camel.CamelContextBuilder;
import org.sonatype.goodies.dropwizard.worker.internal.SnsEventProducerConfiguration;
import org.sonatype.goodies.dropwizard.worker.internal.SnsEventProducerSupport;

import com.amazonaws.services.sns.AmazonSNS;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * {@link WorkEventEnvelope} producer.
 *
 * @since ???
 */
public class WorkEventEnvelopeProducer
    extends SnsEventProducerSupport
{
  @Inject
  public WorkEventEnvelopeProducer(final Provider<CamelContextBuilder> camelContextBuilder,
                                   final AmazonSNS snsClient,
                                   @Assisted final SnsEventProducerConfiguration configuration)
  {
    super(
        camelContextBuilder,
        snsClient,
        configuration,
        WorkEventEnvelope.DATA_FORMAT,
        WorkEventEnvelope.SUBJECT
    );
  }

  public void post(final WorkEventEnvelope envelope) {
    start(envelope);
  }

  //
  // Factory
  //

  public interface Factory
  {
    WorkEventEnvelopeProducer create(SnsEventProducerConfiguration configuration);
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
