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
package org.sonatype.goodies.dropwizard.security;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.util.LifecycleUtils;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Default {@link SecurityService}.
 *
 * @since ???
 */
@Named
@Singleton
@Priority(100_000)
public class SecurityServiceImpl
    extends ServiceSupport
    implements SecurityService
{
  private final SecurityConfiguration configuration;

  private final RealmSecurityManager securityManager;

  private final Map<String, Realm> realms;

  @Inject
  public SecurityServiceImpl(final SecurityConfiguration configuration,
                             final RealmSecurityManager securityManager,
                             final Map<String, Realm> realms)
  {
    this.configuration = checkNotNull(configuration);
    this.securityManager = checkNotNull(securityManager);
    this.realms = checkNotNull(realms);
  }

  @Override
  protected void doStart() throws Exception {
    log.debug("Available realms: {}", realms);

    Set<Realm> enabled = new LinkedHashSet<>();
    for (String name : configuration.getRealms()) {
      Realm realm = realms.get(name);
      checkState(realm != null, "Invalid realm: %s", name);
      log.debug("Enable realm: {} -> {}", name, realm);

      // TODO: resolve what else is needed to properly initialize/inject realm instances
      LifecycleUtils.init(realm);

      enabled.add(realm);
    }

    checkState(!realms.isEmpty(), "No realms were enabled");

    securityManager.setRealms(enabled);
  }
}
