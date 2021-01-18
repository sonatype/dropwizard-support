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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Jersey module.
 *
 * @since ???
 */
public class JerseyModule
    extends AbstractModule
{
  private static final Logger log = LoggerFactory.getLogger(JerseyModule.class);

  private final Environment environment;

  public JerseyModule(final Environment environment) {
    this.environment = checkNotNull(environment);
  }

  @Provides
  ServletContainer getServletContainer() {
    return (ServletContainer) environment.getJerseyServletContainer();
  }

  @Provides
  ApplicationHandler getApplicationHandler() {
    return getServletContainer().getApplicationHandler();
  }

  @Provides
  InjectionManager getInjectionManager() {
    return getApplicationHandler().getInjectionManager();
  }

  @Provides
  ServiceLocator getServiceLocator() {
    return getInjectionManager().getInstance(ServiceLocator.class);
  }

  //
  // @Context support
  //

  private <T> T context(final Class<T> type) {
    T result = getServiceLocator().getService(type);
    log.trace("Context: {} -> {}", type, result);
    return result;
  }

  @Provides
  Application getApplication() {
    return context(Application.class);
  }

  @Provides
  SecurityContext getSecurityContext() {
    return context(SecurityContext.class);
  }

  @Provides
  Providers getProviders() {
    return context(Providers.class);
  }

  @Provides
  UriInfo getUriInfo() {
    return context(UriInfo.class);
  }

  @Provides
  Request getRequest() {
    return context(Request.class);
  }

  @Provides
  HttpServletRequest getHttpServletRequest() {
    return context(HttpServletRequest.class);
  }

  @Provides
  HttpServletResponse getHttpServletResponse() {
    return context(HttpServletResponse.class);
  }

  @Provides
  HttpHeaders getHttpHeaders() {
    return context(HttpHeaders.class);
  }
}

