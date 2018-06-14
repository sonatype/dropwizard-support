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
package com.sonatype.ossindex.dropwizard.jersey;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.hk2.utilities.Binder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.inmemory.ExposedInMemoryConnectorProvider;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link TestContainer} which exposes {@link Binder}.
 *
 * @since ???
 */
public class BindableTestContainer
    implements TestContainer
{
  private static final Logger log = LoggerFactory.getLogger(BindableTestContainer.class);

  private final URI baseUri;

  private final ApplicationHandler handler;

  public BindableTestContainer(final URI baseUri, final DeploymentContext context, final Binder binder) {
    checkNotNull(baseUri);
    checkNotNull(context);
    checkNotNull(binder);
    this.baseUri = UriBuilder.fromUri(baseUri).path(context.getContextPath()).build();
    this.handler = new ApplicationHandler(context.getResourceConfig(), binder);
  }

  @Override
  public URI getBaseUri() {
    return baseUri;
  }

  public ApplicationHandler getHandler() {
    return handler;
  }

  @Override
  public ClientConfig getClientConfig() {
    return new ClientConfig().connectorProvider(new ExposedInMemoryConnectorProvider(baseUri, handler));
  }

  @Override
  public void start() {
    log.debug("Started");
  }

  @Override
  public void stop() {
    log.debug("Stopped");
  }

  public static class Factory
      implements TestContainerFactory
  {
    private final Binder binder;

    public Factory(final Binder binder) {
      this.binder = checkNotNull(binder);
    }

    @Override
    public TestContainer create(final URI baseUri, final DeploymentContext context) {
      return new BindableTestContainer(baseUri, context, binder);
    }
  }
}
