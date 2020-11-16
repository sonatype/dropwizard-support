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

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.InjectionManagerProvider;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import static com.google.common.base.Preconditions.checkNotNull;

// SEE: https://github.com/eclipse-ee4j/glassfish-hk2/tree/master/guice-bridge

/**
 * Configures Jersey with {@link GuiceIntoHK2Bridge}.
 *
 * @since 1.0.0
 */
public class JerseyGuiceBridgeFeature
    implements Feature
{
  private final Injector injector;

  public JerseyGuiceBridgeFeature(final Injector injector) {
    this.injector = checkNotNull(injector);
  }

  @Override
  public boolean configure(final FeatureContext context) {
    InjectionManager injection = InjectionManagerProvider.getInjectionManager(context);
    ServiceLocator locator = injection.getInstance(ServiceLocator.class);
    GuiceBridge.getGuiceBridge().initializeGuiceBridge(locator);
    GuiceIntoHK2Bridge bridge = locator.getService(GuiceIntoHK2Bridge.class);
    bridge.bridgeGuiceInjector(injector);
    return true;
  }
}