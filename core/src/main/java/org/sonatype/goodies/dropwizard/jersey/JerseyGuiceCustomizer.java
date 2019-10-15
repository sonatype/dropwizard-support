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
package org.sonatype.goodies.dropwizard.jersey;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.UriInfo;

import org.sonatype.goodies.dropwizard.app.ApplicationCustomizer;
import org.sonatype.goodies.dropwizard.app.ApplicationSupport;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: there may be a better way to do this with HK2IntoGuiceBridge and/or HK2ToGuiceTypeListenerImpl
// TODO: but due to how Sisu boots up to setup DW and DW sets up Jersey there is a chicken-egg problem here

/**
 * Provides support for injecting select Jersey components into Guice components.
 *
 * @since ???
 */
public class JerseyGuiceCustomizer
    implements ApplicationCustomizer
{
  private static final Logger log = LoggerFactory.getLogger(JerseyGuiceCustomizer.class);

  /**
   * Jersey/HK2 service-locator reference.
   */
  private final AtomicReference<ServiceLocator> locatorHandle = new AtomicReference<>();

  /**
   * Attempt to lookup given component type.
   */
  @Nullable
  private <T> T lookup(final Class<T> type) {
    ServiceLocator locator = locatorHandle.get();
    if (locator == null) {
      log.warn("Service-locator not bound; unable to lookup: {}", type);
      return null;
    }

    T result = locator.getService(type);
    log.trace("Lookup: {} -> {}", type, result);
    return result;
  }

  private class JerseyModule
    implements Module
  {
    @Override
    public void configure(final Binder binder) {
      // empty
    }

    @Provides
    @Nullable
    ServiceLocator getServiceLocator() {
      return locatorHandle.get();
    }

    @Provides
    @Nullable
    UriInfo getUriInfo() {
      return lookup(UriInfo.class);
    }

    @Provides
    @Nullable
    HttpServletRequest getHttpServletRequest() {
      return lookup(HttpServletRequest.class);
    }
  }

  /**
   * Install {@link JerseyModule}.
   */
  @Override
  public List<Module> modules(final Configuration config, final Environment environment) throws Exception {
    return ImmutableList.of(
        new JerseyModule()
    );
  }

  /**
   * Capture {@link ServiceLocator} reference.
   */
  @Override
  public void customize(final ApplicationSupport application,
                        final Configuration config,
                        final Environment environment)
      throws Exception
  {
    environment.jersey().register((Feature) context -> {
      ServiceLocator locator = ServiceLocatorProvider.getServiceLocator(context);
      log.debug("Service-locator: {}", locator);
      locatorHandle.set(locator);
      return locator != null;
    });
  }
}
