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

import javax.inject.Named;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.servlet.ServletContainer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Provides HK2 {@link ServiceLocator}.
 *
 * @since ???
 */
@Named
public class ServiceLocatorProvider
    implements Provider<ServiceLocator>
{
  private final Environment environment;

  @Inject
  public ServiceLocatorProvider(final Environment environment) {
    this.environment = checkNotNull(environment);
  }

  @Override
  public ServiceLocator get() {
    ServletContainer container = (ServletContainer) environment.getJerseyServletContainer();
    checkState(container != null, "Servlet-container not ready");
    return container.getApplicationHandler().getServiceLocator();
  }
}