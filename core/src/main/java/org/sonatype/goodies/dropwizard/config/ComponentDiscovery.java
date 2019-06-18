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
package org.sonatype.goodies.dropwizard.config;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ExceptionMapper;

import org.sonatype.goodies.dropwizard.env.EnvironmentCustomizer;
import org.sonatype.goodies.dropwizard.jaxrs.Component;
import org.sonatype.goodies.dropwizard.jaxrs.Resource;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Key;
import io.dropwizard.lifecycle.JettyManaged;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.inject.BeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Component discovery.
 *
 * @since 1.0.0
 */
@Named
@Singleton
public class ComponentDiscovery
{
  private static final Logger log = LoggerFactory.getLogger(ComponentDiscovery.class);

  private final BeanLocator locator;

  @Inject
  public ComponentDiscovery(final BeanLocator locator) {
    this.locator = checkNotNull(locator);
  }

  /**
   * Discover components.
   */
  public void discover(final Environment environment) {
    log.debug("Discovering components");

    detectHealthChecks(environment);
    detectJaxrsComponents(environment);
    detectTasks(environment);
    detectManaged(environment);
    detectCustomizers(environment);

    // log order of life-cycle components
    if (log.isDebugEnabled()) {
      List<LifeCycle> components = environment.lifecycle().getManagedObjects();
      log.debug("{} life-cycle components:", components.size());
      for (LifeCycle component : components) {
        if (component instanceof JettyManaged) {
          Managed managed = ((JettyManaged) component).getManaged();
          log.debug("  {}", managed);
        }
      }
    }
  }

  /**
   * Locate components for given type with optional specific qualifier annotation.
   */
  @SuppressWarnings("unchecked")
  private <Q extends Annotation, C> Iterable<BeanEntry<Q, C>> locate(final Class<C> type, @Nullable final Class<Q> annotation) {
    List<BeanEntry<Q, C>> result = new ArrayList<>();

    Key<C> key;
    if (annotation != null) {
      key = Key.get(type, annotation);
    }
    else {
      key = Key.get(type);
    }

    for (BeanEntry entry : locator.locate(key)) {
      result.add(entry);
    }

    return result;
  }

  /**
   * Locate components for given type.
   */
  private <C> Iterable<BeanEntry<Annotation, C>> locate(final Class<C> type) {
    return locate(type, null);
  }

  // TODO: may need a better way to order managed dependencies which are started in order they are added
  // TODO: ... atm must use @Priority to ensure order, which is a bit obnoxious

  /**
   * Detect {@link Managed} components.
   */
  private void detectManaged(final Environment environment) {
    for (BeanEntry<Annotation, Managed> entry : locate(Managed.class)) {
      Managed component = entry.getValue();
      environment.lifecycle().manage(component);
      log.debug("Added managed: {}", component);
    }
  }

  /**
   * Detect {@link Task} components.
   */
  private void detectTasks(final Environment environment) {
    for (BeanEntry<Annotation, Task> entry : locate(Task.class)) {
      Task component = entry.getValue();
      environment.admin().addTask(component);
      log.debug("Added task: {}", component);
    }
  }

  /**
   * Detect {@link HealthCheck} components.
   */
  private void detectHealthChecks(final Environment environment) {
    for (BeanEntry<Named, HealthCheck> entry : locate(HealthCheck.class, Named.class)) {
      String name = entry.getKey().value();
      HealthCheck component = entry.getValue();
      environment.healthChecks().register(name, component);
      log.debug("Added health-check: {} -> {}", name, component);
    }
  }

  /**
   * Detect JAX-RS components.
   */
  private void detectJaxrsComponents(final Environment environment) {
    // can not use @Provider here, as its not a Guice @Qualifier annotation
    for (BeanEntry<Annotation, Component> entry : locate(Component.class)) {
      Component component = entry.getValue();
      environment.jersey().register(component);
      log.debug("Added JAX-RS component: {}", component);
    }

    for (BeanEntry<Annotation, Resource> entry : locate(Resource.class)) {
      Resource component = entry.getValue();
      environment.jersey().register(component);
      log.debug("Added JAX-RS resource: {}", component);
    }

    for (BeanEntry<Annotation, ExceptionMapper> entry : locate(ExceptionMapper.class)) {
      ExceptionMapper component = entry.getValue();
      environment.jersey().register(component);
      log.debug("Added JAX-RS exception-mapper: {}", component);
    }

    for (BeanEntry<Annotation, Feature> entry : locate(Feature.class)) {
      Feature component = entry.getValue();
      environment.jersey().register(component);
      log.debug("Added JAX-RS feature: {}", component);
    }

    for (BeanEntry<Annotation, DynamicFeature> entry : locate(DynamicFeature.class)) {
      DynamicFeature component = entry.getValue();
      environment.jersey().register(component);
      log.debug("Added JAX-RS dynamic-feature: {}", component);
    }
  }

  /**
   * Detect and apply {@link EnvironmentCustomizer} components.
   */
  private void detectCustomizers(final Environment environment) {
    for (BeanEntry<Annotation, EnvironmentCustomizer> entry : locate(EnvironmentCustomizer.class)) {
      EnvironmentCustomizer customizer = entry.getValue();
      log.debug("Applying customizer: {}", customizer);
      customizer.customize(environment);
    }
  }
}
