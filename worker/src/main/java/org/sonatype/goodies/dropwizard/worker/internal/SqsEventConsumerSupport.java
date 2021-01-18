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

import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Provider;

import org.sonatype.goodies.dropwizard.camel.CamelContextBuilder;
import org.sonatype.goodies.dropwizard.camel.sns.SnsDataFormat;
import org.sonatype.goodies.dropwizard.camel.sns.SnsMessageExtractionProcessor;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.amazonaws.services.sqs.AmazonSQS;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.util.URISupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for AWS SQS event consumers.
 *
 * @since ???
 */
public abstract class SqsEventConsumerSupport
    extends ServiceSupport
{
  private final Provider<CamelContextBuilder> camelContextBuilder;

  private final AmazonSQS sqsClient;

  private final SqsEventConsumerConfiguration configuration;

  private final DataFormat dataFormat;

  private final Predicate predicate;

  private final Counter errorCounter;

  @Nullable
  private CamelContext camelContext;

  public SqsEventConsumerSupport(final MetricRegistry metricRegistry,
                                 final Provider<CamelContextBuilder> camelContextBuilder,
                                 final AmazonSQS sqsClient,
                                 final SqsEventConsumerConfiguration configuration,
                                 final DataFormat dataFormat,
                                 final Predicate predicate)
  {
    checkNotNull(metricRegistry);
    this.camelContextBuilder = checkNotNull(camelContextBuilder);
    this.sqsClient = checkNotNull(sqsClient);
    this.configuration = checkNotNull(configuration);
    log.info("Configuration: {}", configuration);

    this.dataFormat = checkNotNull(dataFormat);
    log.debug("Data-format: {}", dataFormat);

    this.predicate = checkNotNull(predicate);
    log.debug("Predicate: {}", predicate);

    this.errorCounter = metricRegistry.counter(MetricRegistry.name("service", getName(), "errors"));
  }

  protected String getName() {
    return getClass().getSimpleName();
  }

  @Override
  protected void doStart() throws Exception {
    camelContext = createCamelContext();
    camelContext.start();
  }

  private CamelContext createCamelContext() throws Exception {
    DefaultCamelContext camelContext = camelContextBuilder.get()
        .name(getName())
        .logger(log)
        .bind("sqs-client", sqsClient)
        .build();

    camelContext.addRoutes(new RouteBuilder()
    {
      @Override
      public void configure() throws Exception {
        errorHandler(defaultErrorHandler()
            .disableRedelivery()
            .onExceptionOccurred(exchange -> {
              errorCounter.inc();
            })
        );

        String queueUri = String.format("aws-sqs://%s", configuration.getQueue());
        Map<String, Object> queueOptions = ImmutableMap.<String, Object>builder()
            .put("amazonSQSClient", "#sqs-client")
            .put("autoCreateQueue", false)
            .put("concurrentConsumers", configuration.getConcurrentConsumers())
            .put("maxMessagesPerPoll", configuration.getMaxMessagesPerPoll())
            .put("waitTimeSeconds", configuration.getWaitTime().toSeconds())
            .put("defaultVisibilityTimeout", configuration.getVisibilityTimeout().toSeconds())
            .put("extendMessageVisibility", true)
            .put("visibilityTimeout", configuration.getVisibilityTimeout().toSeconds())
            .put("deleteIfFiltered", false)
            .build();

        from(URISupport.appendParametersToURI(queueUri, queueOptions))
            .id("start")
            .log(LoggingLevel.TRACE, "Payload: ${in.body}")
            .unmarshal(SnsDataFormat.create(Map.class))
            .log(LoggingLevel.TRACE, "Decoded: ${in.body}")
            .choice()
            .when(predicate).to("direct:consume")
            .otherwise().to("direct:unhandled");

        from("direct:consume")
            .id("consume")
            .process(new SnsMessageExtractionProcessor())
            .unmarshal(dataFormat)
            .log(LoggingLevel.TRACE, "Consume: ${in.body}")
            .process(exchange -> consume(exchange.getIn().getMandatoryBody()));

        from("direct:unhandled")
            .id("unhandled")
            .log(LoggingLevel.WARN, "Unhandled: ${exchange}; body: ${in.body}");
      }
    });

    return camelContext;
  }

  @Override
  protected void doStop() {
    if (camelContext != null) {
      camelContext.stop();
      camelContext = null;
    }
  }

  protected abstract void consume(Object event);
}
