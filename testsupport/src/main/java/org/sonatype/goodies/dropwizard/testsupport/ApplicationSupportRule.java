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
package org.sonatype.goodies.dropwizard.testsupport;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.sonatype.goodies.dropwizard.ApplicationCustomizer;
import org.sonatype.goodies.dropwizard.ApplicationSupport;
import org.sonatype.goodies.dropwizard.client.endpoint.EndpointFactory;
import org.sonatype.goodies.dropwizard.client.endpoint.EndpointFactory.Builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.cli.Command;
import io.dropwizard.cli.ServerCommand;
import io.dropwizard.jersey.jackson.JacksonBinder;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.DropwizardTestSupport.ServiceListener;
import io.dropwizard.testing.ResourceHelpers;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support rule for application tests.
 *
 * @since ???
 */
public class ApplicationSupportRule<T extends ApplicationSupport<C>, C extends Configuration>
    extends ExternalResource
{
  protected final Logger log = LoggerFactory.getLogger(getClass());

  private final AtomicInteger recursiveCallCount = new AtomicInteger(0);

  private final Class<? extends T> applicationClass;

  @Nullable
  protected String configPath;

  @Nullable
  protected String customPropertyPrefix;

  protected Function<Application<C>, Command> commandInstantiator = ServerCommand::new;

  protected final Set<ConfigOverride> configOverrides = new LinkedHashSet<>();

  protected final List<Module> modules = new ArrayList<>();

  protected final List<ApplicationCustomizer> customizers = new ArrayList<>();

  private final DropwizardTestSupport<C> delegate;

  protected final Callable<JerseyClientBuilder> defaultClientBuilder = () -> new JerseyClientBuilder()
      .register(new JacksonBinder(getObjectMapper()))
      .property(ClientProperties.CONNECT_TIMEOUT, 1000)
      .property(ClientProperties.READ_TIMEOUT, 5000)
      .property(ClientProperties.FOLLOW_REDIRECTS, true)
      .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);

  protected Callable<JerseyClientBuilder> clientBuilder = defaultClientBuilder;

  @Nullable
  private Client client;

  public ApplicationSupportRule(final Class<? extends T> applicationClass) {
    this.applicationClass = checkNotNull(applicationClass);

    log.info("Application-class: {}", applicationClass);

    try {
      configure();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    log.info("Config-path: {}", configPath);
    log.info("Custom property-prefix: {}", customPropertyPrefix);
    log.info("Command instantiator: {}", commandInstantiator);
    log.info("Config overrides: {}", configOverrides);

    delegate = new DropwizardTestSupport<>(
        applicationClass,
        configPath,
        Optional.ofNullable(customPropertyPrefix),
        commandInstantiator,
        configOverrides.toArray(new ConfigOverride[0])
    );

    log.info("Modules: {}", modules);
    log.info("Customizers: {}", customizers);

    // register listener to configure application
    delegate.addListener(new ServiceListener<C>() {
      @Override
      public void onRun(final C configuration, final Environment environment, final DropwizardTestSupport<C> rule)
          throws Exception
      {
        configure(getApplication());
      }
    });
  }

  /**
   * Rule configuration hook.
   */
  protected void configure() throws Exception {
    // empty
  }

  /**
   * Application configuration hook.
   */
  protected void configure(final T application) throws Exception {
    // if any modules are configured then add customizer
    if (!modules.isEmpty()) {
      application.addCustomizer(new ApplicationCustomizer()
      {
        @Override
        public List<Module> modules(final Configuration config, final Environment environment) {
          return ImmutableList.copyOf(modules);
        }
      });
    }

    // add any other customizers
    application.addCustomizer(ImmutableList.copyOf(customizers));
  }

  //
  // ExternalResource
  //

  private final Stopwatch watch = Stopwatch.createUnstarted();

  @Override
  protected void before() {
    if (recursiveCallCount.getAndIncrement() == 0) {
      log.info("Starting application: {}", applicationClass.getName());
      watch.reset().start();
      delegate.before();
    }
  }

  @Override
  protected void after() {
    if (recursiveCallCount.decrementAndGet() == 0) {
      delegate.after();
      synchronized (this) {
        if (client != null) {
          client.close();
        }
        client = null;
      }
      log.info("Application stopped; {}", watch.stop());
    }
  }

  //
  // Helpers
  //

  public DropwizardTestSupport<C> getDelegate() {
    return delegate;
  }

  public T getApplication() {
    return delegate.getApplication();
  }

  public C getConfiguration() {
    return delegate.getConfiguration();
  }

  public int getLocalPort() {
    return delegate.getLocalPort();
  }

  public int getPort(final int connectorIndex) {
    return delegate.getPort(connectorIndex);
  }

  public int getAdminPort() {
    return delegate.getAdminPort();
  }

  // TODO: adjust urls to include context-path and/or if we have actually connector hostname/ips

  public URI getBaseUrl() {
    // trailing "/" is important
    return URI.create(String.format("http://localhost:%s/", getLocalPort()));
  }

  public URI getAdminUrl() {
    // trailing "/" is important
    return URI.create(String.format("http://localhost:%s/", getAdminPort()));
  }

  public Environment getEnvironment() {
    return delegate.getEnvironment();
  }

  public ObjectMapper getObjectMapper() {
    return delegate.getObjectMapper();
  }

  //
  // Client
  //

  public Client client() {
    synchronized (this) {
      if (client == null) {
        try {
          client = clientBuilder.call().build();
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      return client;
    }
  }

  //
  // Components
  //

  public Injector getInjector() {
    return getApplication().getInjector();
  }

  public <C1> C1 getInstance(final Class<C1> type) {
    return getApplication().getInstance(type);
  }

  //
  // Endpoint
  //

  public <E> E endpoint(final Class<E> type, @Nullable final String path) {
    checkNotNull(type);
    WebTarget target = client().target(getBaseUrl());
    if (path != null) {
      target = target.path(path);
    }
    return EndpointFactory.create(type, target);
  }

  public <E> E endpoint(final Class<E> type) {
    return endpoint(type, null);
  }

  public <E> E endpoint(final Class<E> type, @Nullable final String path, final Consumer<Builder<E>> configurator) {
    checkNotNull(type);
    checkNotNull(configurator);
    WebTarget target = client().target(getBaseUrl());
    if (path != null) {
      target = target.path(path);
    }
    Builder<E> builder = EndpointFactory.builder(type, target);
    configurator.accept(builder);
    return builder.build();
  }

  //
  // Helpers
  //

  public static String resolveConfigPath(final URL url) {
    checkNotNull(url);
    try {
      return new File(url.toURI()).getAbsolutePath();
    }
    catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static String resolveConfigPath(final String path) {
    checkNotNull(path);
    return ResourceHelpers.resourceFilePath(path);
  }

  @SuppressWarnings("UnstableApiUsage")
  public static String resolveConfigPath(final Class<?> owner, final String path) {
    return resolveConfigPath(Resources.getResource(owner, path));
  }
}
