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
package org.sonatype.goodies.dropwizard.client;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;

import org.sonatype.goodies.dropwizard.jersey.LoggingConfiguration;
import org.sonatype.goodies.dropwizard.jersey.LoggingFeature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link JerseyClientFactory}.
 *
 * @since 1.0.2
 */
@Named
@Singleton
public class JerseyClientFactoryImpl
  implements JerseyClientFactory
{
  private static final Logger log = LoggerFactory.getLogger(JerseyClientFactoryImpl.class);

  private final Provider<Environment> environment;

  private final UserAgentSupplier userAgent;

  @Inject
  public JerseyClientFactoryImpl(final Provider<Environment> environment, final UserAgentSupplier userAgent) {
    this.environment = checkNotNull(environment);
    this.userAgent = checkNotNull(userAgent);
  }

  @Override
  public Client create(final ExtJerseyClientConfiguration config,
                       final String name,
                       @Nullable final ClientCustomizer customizer)
  {
    checkNotNull(config);

    JerseyClientBuilder builder = new JerseyClientBuilder(environment.get())
        .using(config)
        .using(createObjectMapper());

    if (customizer != null) {
      log.debug("Applying customizer: {}", customizer);
      customizer.customizer(builder);
    }

    Client client = builder.build(name);

    // due to config.setUserAgent() not working, set header here if not already set
    client.register(new UserAgentRequestFilter(userAgent));

    // maybe configure logging
    LoggingConfiguration lconfig = config.getLoggingConfiguration();
    if (lconfig.isEnabled()) {
      client.register(new LoggingFeature(lconfig));
    }

    return client;
  }

  @Override
  public Client create(final ExtJerseyClientConfiguration config, final String name) {
    return create(config, name, null);
  }

  /**
   * Create default (lax) {@link ObjectMapper} for clients.
   *
   * To override see {@link ClientCustomizer}.
   */
  public static ObjectMapper createObjectMapper() {
    ObjectMapper objectMapper = Jackson.newObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper;
  }
}
