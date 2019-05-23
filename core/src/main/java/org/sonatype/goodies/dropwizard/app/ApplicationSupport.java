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
package org.sonatype.goodies.dropwizard.app;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.sonatype.goodies.dropwizard.config.ComponentDiscovery;
import org.sonatype.goodies.dropwizard.config.ConfigurationModule;
import org.sonatype.goodies.dropwizard.env.BasicEnvironmentReporter;
import org.sonatype.goodies.dropwizard.env.EnvironmentModule;
import org.sonatype.goodies.dropwizard.env.EnvironmentReporter;
import org.sonatype.goodies.dropwizard.jersey.JerseyGuiceBridgeFeature;
import org.sonatype.goodies.dropwizard.metrics.MetricsAopModule;
import org.sonatype.goodies.dropwizard.selection.ComponentSelectionConfiguration;
import org.sonatype.goodies.dropwizard.selection.ComponentSelectionConfigurationAware;
import org.sonatype.goodies.dropwizard.selection.ComponentSelectionTypeListener;
import org.sonatype.goodies.dropwizard.util.FileHelper;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.QualifiedTypeVisitor;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Support for DropWizard Guice+Sisu applications.
 *
 * @see ApplicationCustomizer
 * @see ComponentDiscovery
 * @since 1.0.0
 */
public abstract class ApplicationSupport<T extends Configuration>
    extends Application<T>
{
  private static final Logger log = LoggerFactory.getLogger(ApplicationSupport.class);

  private final List<ApplicationCustomizer> customizers = new ArrayList<>();

  @Nullable
  private EnvironmentReporter environmentReporter = new BasicEnvironmentReporter();

  private Injector injector;

  /**
   * Add application customizers.
   */
  @VisibleForTesting
  public void addCustomizer(final ApplicationCustomizer... customizers) {
    checkNotNull(customizers);
    this.customizers.addAll(Arrays.asList(customizers));
  }

  /**
   * Add application customizers.
   */
  @VisibleForTesting
  public void addCustomizer(final List<ApplicationCustomizer> customizers) {
    checkNotNull(customizers);
    this.customizers.addAll(customizers);
  }

  /**
   * Set environment reporter.
   *
   * @since ???
   */
  public void setEnvironmentReporter(@Nullable final EnvironmentReporter environmentReporter) {
    this.environmentReporter = environmentReporter;
  }

  /**
   * Initialize application.
   *
   * @see #init(Bootstrap)
   */
  @Override
  public final void initialize(final Bootstrap<T> bootstrap) {
    checkNotNull(bootstrap);
    init(bootstrap);

    // allow customizer to contribute bootstrap
    for (ApplicationCustomizer customizer : customizers) {
      log.debug("Customizer initialize: {}", customizer);
      try {
        //noinspection unchecked
        customizer.initialize(bootstrap);
      }
      catch (Exception e) {
        log.error("Customizer failed; aborting", e);
        Throwables.throwIfUnchecked(e);
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Customize application initialisation.
   */
  protected void init(final Bootstrap<T> bootstrap) {
    // empty
  }

  /**
   * Return application injector instance.
   *
   * @throws IllegalStateException Injection not ready
   */
  public Injector getInjector() {
    checkState(injector != null, "Injection not ready");
    return injector;
  }

  /**
   * Lookup a typed component.
   *
   * @see #getInjector()
   */
  public <C> C getInstance(final Class<C> type) {
    checkNotNull(type);
    return getInjector().getInstance(type);
  }

  /**
   * Customize bean-scanning.
   *
   * Defaults to {@link BeanScanning#INDEX}.
   */
  @SuppressWarnings({"WeakerAccess", "unused"})
  protected BeanScanning scanning(final T config) {
    return BeanScanning.INDEX;
  }

  /**
   * Create application {@link Injector}.
   */
  private Injector createInjector(final T config, final Environment environment) {
    List<Module> modules = new ArrayList<>();

    // add binding for application configuration
    modules.add(new ConfigurationModule(config));

    // configure various environment bindings
    modules.add(new EnvironmentModule(environment));

    // configure support for Guice-AOP metrics
    modules.add(new MetricsAopModule(environment));

    // allow customizer to contribute modules
    for (ApplicationCustomizer customizer : customizers) {
      log.debug("Customizer modules: {}", customizer);
      try {
        //noinspection unchecked
        modules.addAll(customizer.modules(config, environment));
      }
      catch (Exception e) {
        log.error("Customizer failed; aborting", e);
        Throwables.throwIfUnchecked(e);
        throw new RuntimeException(e);
      }
    }

    BeanScanning scanning = scanning(config);

    if (log.isDebugEnabled()) {
      log.debug("Scanning: {}", scanning);
      log.debug("Modules:");
      for (Module module : modules) {
        log.debug("  {}", module);
      }
    }

    ClassSpace space = new URLClassSpace(getClass().getClassLoader());
    SpaceModule spaceModule = new SpaceModule(space, scanning);

    // when configuration is ComponentSelectionConfiguration aware then apply selection
    if (config instanceof ComponentSelectionConfigurationAware) {
      ComponentSelectionConfiguration selectionConfiguration = ((ComponentSelectionConfigurationAware)config).getComponentSelectionConfiguration();
      SpaceModule.Strategy strategy = binder -> new QualifiedTypeVisitor(new ComponentSelectionTypeListener(binder, selectionConfiguration));
      spaceModule.with(strategy);
    }

    modules.add(spaceModule);

    return Guice.createInjector(new WireModule(modules));
  }

  /**
   * Prepare application injection and detect components.
   */
  @Override
  public final void run(final T config, final Environment environment) {
    checkNotNull(config);
    checkNotNull(environment);

    if (environmentReporter != null) {
      try {
        environmentReporter.report(LoggerFactory.getLogger(getClass()));
      }
      catch (Exception e) {
        log.warn("Failed to display environment", e);
      }
    }

    injector = createInjector(config, environment);
    log.debug("Injection ready");

    injector.injectMembers(this);

    for (ApplicationCustomizer customizer : customizers) {
      log.debug("Customizer customize: {}", customizer);
      try {
        //noinspection unchecked
        customizer.customize(this, config, environment);
      }
      catch (Exception e) {
        log.error("Customizer failed; aborting", e);
        Throwables.throwIfUnchecked(e);
        throw new RuntimeException(e);
      }
    }

    // install guice bridge into jersey hk2
    environment.jersey().register(new JerseyGuiceBridgeFeature(injector));

    // discover components
    ComponentDiscovery discovery = injector.getInstance(ComponentDiscovery.class);
    discovery.discover(environment);
  }

  @Override
  public final void run(final String... arguments) throws Exception {
    checkNotNull(arguments);

    // early verification that temporary directory is valid
    File tmpdir = FileHelper.tmpdir();
    try {
      Path tmp = Files.createTempFile(getName(), ".tmp");
      Files.delete(tmp);
    }
    catch (Throwable t) {
      Error fatal = new Error("Unable to create temporary file", t);
      System.err.printf("FATAL: %s; check that directory exists and is writable: %s%n", fatal.getMessage(), tmpdir);
      throw fatal;
    }

    Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
    super.run(arguments);
  }

  /**
   * Invoked on application shutdown.
   */
  @SuppressWarnings("WeakerAccess")
  protected void onShutdown() {
    log.info("Shutting down");
  }

  /**
   * Invoked on fatal error before shutdown.
   */
  @Override
  protected void onFatalError() {
    log.error("Fatal error detected; shutting down");
    super.onFatalError();
  }
}
