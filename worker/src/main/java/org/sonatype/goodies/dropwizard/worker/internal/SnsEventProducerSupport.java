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
import org.sonatype.goodies.dropwizard.camel.ExchangeHelper2;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.amazonaws.services.sns.AmazonSNS;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.sns.SnsConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.util.URISupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for AWS SNS event producers.
 *
 * @since ???
 */
public class SnsEventProducerSupport
    extends ServiceSupport
{
  private final Provider<CamelContextBuilder> camelContextBuilder;

  private final AmazonSNS snsClient;

  private final SnsEventProducerConfiguration configuration;

  private final DataFormat dataFormat;

  private final String subject;

  private final Counter errorCounter;

  @Nullable
  private CamelContext camelContext;

  public SnsEventProducerSupport(final MetricRegistry metricRegistry,
                                 final Provider<CamelContextBuilder> camelContextBuilder,
                                 final AmazonSNS snsClient,
                                 final SnsEventProducerConfiguration configuration,
                                 final DataFormat dataFormat,
                                 final String subject)
  {
    checkNotNull(metricRegistry);
    this.camelContextBuilder = checkNotNull(camelContextBuilder);
    this.snsClient = checkNotNull(snsClient);
    this.configuration = checkNotNull(configuration);
    log.info("Configuration: {}", configuration);

    this.dataFormat = checkNotNull(dataFormat);
    log.debug("Data-format: {}", dataFormat);

    this.subject = checkNotNull(subject);
    log.debug("Subject: {}", subject);

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
        .bind("sns-client", snsClient)
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

        String topicUri = String.format("aws-sns://%s", configuration.getTopic());
        Map<String, Object> topicOptions = ImmutableMap.<String, Object>builder()
            .put("amazonSNSClient", "#sns-client")
            .put("autoCreateTopic", false)
            .build();

        from("direct:start")
            .id("start")
            .marshal(dataFormat)
            .process(exchange -> {
              // attach SNS subject to message
              Message out = ExchangeHelper2.copyIn(exchange);
              out.setHeader(SnsConstants.SUBJECT, subject);
            })
            .log(LoggingLevel.TRACE, "Body: ${in.body}")
            .to(URISupport.appendParametersToURI(topicUri, topicOptions));

        from("direct:unprocessed")
            .id("unprocessed")
            .log(LoggingLevel.WARN, "Unprocessed: ${exchange}; body: ${in.body}");
      }
    });

    return camelContext;
  }

  @Override
  protected void doStop() throws Exception {
    if (camelContext != null) {
      camelContext.stop();
      camelContext = null;
    }
  }

  protected void start(final Object event) {
    checkNotNull(event);
    ensureStarted();
    log.trace("Start: {}", event);

    assert camelContext != null;
    ProducerTemplate producer = camelContext.createProducerTemplate();
    producer.sendBody("direct:start", event);
  }
}
