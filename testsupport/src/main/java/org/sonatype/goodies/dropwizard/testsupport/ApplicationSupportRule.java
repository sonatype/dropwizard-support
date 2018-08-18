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

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.sonatype.goodies.dropwizard.ApplicationCustomizer;
import org.sonatype.goodies.dropwizard.ApplicationSupport;
import org.sonatype.goodies.dropwizard.client.endpoint.EndpointFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
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
  private String configPath;

  @Nullable
  private String customPropertyPrefix;

  private Function<Application<C>, Command> commandInstantiator = ServerCommand::new;

  private final Set<ConfigOverride> configOverrides = new LinkedHashSet<>();

  private final List<Module> modules = new ArrayList<>();

  private final List<ApplicationCustomizer> customizers = new ArrayList<>();

  private final DropwizardTestSupport<C> delegate;

  @Nullable
  private Client client;

  public interface Configurator
  {
    void configure(ApplicationSupportRule rule) throws Exception;
  }

  public ApplicationSupportRule(final Class<? extends T> applicationClass,
                                final Configurator configurator)
  {
    this.applicationClass = checkNotNull(applicationClass);
    checkNotNull(configurator);

    try {
      configurator.configure(this);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    delegate = new DropwizardTestSupport<>(
        applicationClass,
        configPath,
        Optional.ofNullable(customPropertyPrefix),
        commandInstantiator,
        configOverrides.toArray(new ConfigOverride[0])
    );

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

  //
  // Configuration
  //

  @Nullable
  public String getConfigPath() {
    return configPath;
  }

  public void setConfigPath(@Nullable final String configPath) {
    this.configPath = configPath;
  }

  @Nullable
  public String getCustomPropertyPrefix() {
    return customPropertyPrefix;
  }

  public void setCustomPropertyPrefix(@Nullable final String customPropertyPrefix) {
    this.customPropertyPrefix = customPropertyPrefix;
  }

  public Function<Application<C>, Command> getCommandInstantiator() {
    return commandInstantiator;
  }

  public void setCommandInstantiator(final Function<Application<C>, Command> commandInstantiator) {
    this.commandInstantiator = checkNotNull(commandInstantiator);
  }

  public Set<ConfigOverride> getConfigOverrides() {
    return configOverrides;
  }

  public void addConfigOverride(final ConfigOverride configOverride) {
    checkNotNull(configOverride);
    getConfigOverrides().add(configOverride);
  }

  public List<Module> getModules() {
    return modules;
  }

  public void addModule(final Module module) {
    checkNotNull(module);
    modules.add(module);
  }

  public List<ApplicationCustomizer> getCustomizers() {
    return customizers;
  }

  public void addCustomizer(final ApplicationCustomizer customizer) {
    checkNotNull(customizer);
    customizers.add(customizer);
  }

  // TODO: expose listeners for config?
  // TODO: exposed managed for config?

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
      watch.start();
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
      }
      log.info("Application stopped; {}", watch);
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

  private static final int DEFAULT_CONNECT_TIMEOUT_MS = 1000;

  private static final int DEFAULT_READ_TIMEOUT_MS = 5000;

  protected JerseyClientBuilder clientBuilder() {
    return new JerseyClientBuilder()
        .register(new JacksonBinder(getObjectMapper()))
        .property(ClientProperties.CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT_MS)
        .property(ClientProperties.READ_TIMEOUT, DEFAULT_READ_TIMEOUT_MS)
        .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
  }

  public Client client() {
    synchronized (this) {
      if (client == null) {
        client = clientBuilder().build();
      }
      return client;
    }
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
}
