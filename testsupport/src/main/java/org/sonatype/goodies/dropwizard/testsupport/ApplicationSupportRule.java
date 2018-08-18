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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.ws.rs.client.WebTarget;

import org.sonatype.goodies.dropwizard.ApplicationCustomizer;
import org.sonatype.goodies.dropwizard.ApplicationSupport;
import org.sonatype.goodies.dropwizard.client.endpoint.EndpointFactory;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.cli.Command;
import io.dropwizard.cli.ServerCommand;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

// NOTE: Groovy does not like generic types here, so using Java

// FIXME: consider unrolling DropwizardAppRule and just directly delegating to DropwizardTestSupport

/**
 * Support rule for application tests.
 *
 * @since ???
 */
public class ApplicationSupportRule<T extends ApplicationSupport<C>, C extends Configuration>
    extends DropwizardAppRule<C>
{
  private static final Logger log = LoggerFactory.getLogger(ApplicationSupportRule.class);

  private final List<ApplicationCustomizer> customizers = new ArrayList<>();

  /**
   * See {@link Builder}.
   */
  private ApplicationSupportRule(final Class<? extends Application<C>> applicationClass,
                                 @Nullable final String configPath,
                                 final Optional<String> customPropertyPrefix,
                                 final Function<Application<C>, Command> commandInstantiator,
                                 final ConfigOverride... configOverrides)
  {
    super(applicationClass, configPath, customPropertyPrefix, commandInstantiator, configOverrides);

    // register listener to install application customizers
    addListener(new ServiceListener<C>()
    {
      @Override
      public void onRun(final C configuration, final Environment environment, final DropwizardAppRule<C> rule)
          throws Exception
      {
        getApplication().addCustomizer(customizers);
      }
    });
  }

  public ApplicationSupportRule(final Class<? extends ApplicationSupport<C>> type,
                                @Nullable final String configPath,
                                final ConfigOverride... configOverrides)
  {
    this(type, configPath, Optional.empty(), ServerCommand::new, configOverrides);
  }

  public ApplicationSupportRule<T, C> addCustomizer(final ApplicationCustomizer... customizers) {
    checkNotNull(customizers);
    Collections.addAll(this.customizers, customizers);
    return this;
  }

  @SuppressWarnings("unchecked")
  public ApplicationSupport<C> getApplication() {
    return super.getApplication();
  }

  public URI getBaseUrl() {
    // trailing "/" is important
    return URI.create(String.format("http://localhost:%s/", getLocalPort()));
  }

  public URI getAdminUrl() {
    // trailing "/" is important
    return URI.create(String.format("http://localhost:%s/", getAdminPort()));
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

  //
  // Builder
  //

  public static class Builder<T extends ApplicationSupport<C>, C extends Configuration>
  {
    private Class<? extends T> type;

    private String configPath;

    private String customPropertyPrefix;

    private Function<Application<C>, Command> commandInstantiator = ServerCommand::new;

    private Set<ConfigOverride> configOverrides = Collections.emptySet();

    private final List<ApplicationCustomizer> customizers = new ArrayList<>();

    public Builder() {
      // empty
    }

    public Builder(final Function<Builder<T, C>, Void> function) {
      function.apply(this);
    }

    public Builder<T, C> type(final Class<? extends T> type) {
      this.type = type;
      return this;
    }

    public Builder<T, C> configPath(final String configPath) {
      this.configPath = configPath;
      return this;
    }

    public Builder<T, C> configOverrides(final Set<ConfigOverride> configOverrides) {
      this.configOverrides = configOverrides;
      return this;
    }

    public Builder<T, C> customPropertyPrefix(final String customPropertyPrefix) {
      this.customPropertyPrefix = customPropertyPrefix;
      return this;
    }

    public Builder<T, C> commandInstantiator(final Function<Application<C>, Command> commandInstantiator) {
      this.commandInstantiator = commandInstantiator;
      return this;
    }

    public Builder<T, C> customizer(final ApplicationCustomizer customizer) {
      customizers.add(customizer);
      return this;
    }

    public ApplicationSupportRule<T, C> build() {
      checkNotNull(type, "Missing: type");

      ApplicationSupportRule<T, C> rule = new ApplicationSupportRule<>(
          type,
          configPath,
          Optional.ofNullable(customPropertyPrefix),
          commandInstantiator,
          configOverrides.toArray(new ConfigOverride[0])
      );

      rule.addCustomizer(customizers.toArray(new ApplicationCustomizer[0]));

      return rule;
    }
  }
}
