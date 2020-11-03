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

import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.servlet.ServletContainer;

// TODO: maybe explicit binding vs. named?

/**
 * Jersey module.
 *
 * @since ???
 */
@Named
public class JerseyModule
    extends AbstractModule
{
  @Override
  protected void configure() {
    // empty
  }

  @Provides
  ServletContainer getServletContainer(final Environment environment) {
    return (ServletContainer) environment.getJerseyServletContainer();
  }

  @Provides
  ApplicationHandler getApplicationHandler(final ServletContainer container) {
    return container.getApplicationHandler();
  }

  @Provides
  InjectionManager getInjectionManager(final ApplicationHandler handler) {
    return handler.getInjectionManager();
  }

  //@Provides
  //ServiceLocator getServiceLocator(final InjectionManager injection) {
  //  return injection.getInstance(ServiceLocator.class);
  //}
}
