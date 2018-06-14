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
package com.sonatype.ossindex.dropwizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sonatype.ossindex.dropwizard.internal.ComponentDiscovery;
import com.sonatype.ossindex.dropwizard.internal.ConfigurationModule;
import com.sonatype.ossindex.dropwizard.internal.EnvironmentModule;
import com.sonatype.ossindex.dropwizard.jersey.JerseyGuiceBridgeFeature;
import com.sonatype.ossindex.dropwizard.security.MdcUserScope;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.ClassSpace;
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
 * @since ???
 * @see ApplicationCustomizer
 * @see ComponentDiscovery
 */
public abstract class ApplicationSupport<T extends Configuration>
    extends Application<T>
{
  private static final Logger log = LoggerFactory.getLogger(ApplicationSupport.class);

  private final List<ApplicationCustomizer> customizers = new ArrayList<>();

  private Injector injector;

  /**
   * Add application customizers.
   */
  protected void addCustomizer(final ApplicationCustomizer... customizers) {
    checkNotNull(customizers);
    this.customizers.addAll(Arrays.asList(customizers));
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
      //noinspection unchecked
      customizer.initialize(bootstrap);
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

    // allow customizer to contribute modules
    for (ApplicationCustomizer customizer : customizers) {
      log.debug("Customizer modules: {}", customizer);
      //noinspection unchecked
      modules.addAll(customizer.modules(config, environment));
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
    modules.add(new SpaceModule(space, scanning));

    return Guice.createInjector(new WireModule(modules));
  }

  /**
   * Prepare application injection and detect components.
   */
  @Override
  public final void run(final T config, final Environment environment) {
    checkNotNull(config);
    checkNotNull(environment);

    displayEnvironment();

    injector = createInjector(config, environment);
    log.debug("Injection ready");

    injector.injectMembers(this);

    for (ApplicationCustomizer customizer : customizers) {
      log.debug("Customizer customize: {}", customizer);
      //noinspection unchecked
      customizer.customize(this, config, environment);
    }

    // install guice bridge into jersey hk2
    environment.jersey().register(new JerseyGuiceBridgeFeature(injector));

    // discover components
    ComponentDiscovery discovery = injector.getInstance(ComponentDiscovery.class);
    discovery.discover(environment);
  }

  /**
   * Display critical information details.
   */
  private void displayEnvironment() {
    Logger log = LoggerFactory.getLogger(getClass());

    log.info("Java: {}, {}, {}, {}",
        System.getProperty("java.version"),
        System.getProperty("java.vm.name"),
        System.getProperty("java.vm.vendor"),
        System.getProperty("java.vm.version")
    );
    log.info("OS: {}, {}, {}",
        System.getProperty("os.name"),
        System.getProperty("os.version"),
        System.getProperty("os.arch")
    );
    log.info("User: {}, {}, {}",
        System.getProperty("user.name"),
        System.getProperty("user.language"),
        System.getProperty("user.home")
    );
    log.info("CWD: {}", System.getProperty("user.dir"));
    log.info("TMP: {}", System.getProperty("java.io.tmpdir"));
  }

  @Override
  public final void run(final String... arguments) throws Exception {
    checkNotNull(arguments);
    MdcUserScope.forSystem();
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
