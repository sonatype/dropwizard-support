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

import javax.inject.Singleton;
import javax.servlet.Filter;

import org.sonatype.goodies.dropwizard.security.realms.local.LocalRealm;

import com.google.common.annotations.Beta;
import com.google.inject.binder.AnnotatedBindingBuilder;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.dropwizard.FirstSuccessfulModularRealmAuthenticator;
import org.apache.shiro.dropwizard.WebSecurityManagerImpl;
import org.apache.shiro.dropwizard.WebSessionManagerImpl;
import org.apache.shiro.guice.ShiroModule;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Security module.
 *
 * @since ???
 */
@Beta
public class SecurityModule
    extends ShiroModule
{
  private final SecurityConfiguration configuration;

  public SecurityModule(final SecurityConfiguration configuration) {
    this.configuration = checkNotNull(configuration);
  }

  // NOTE: overly complex guice bindings here due to ShiroModule being private, and generally having poor integration with guice+sisu

  @Override
  protected void configureShiro() {
    // cache-manager
    bind(CacheManager.class).to(MemoryConstrainedCacheManager.class).in(Singleton.class);
    expose(CacheManager.class);

    // filter-chain resolver
    bind(FilterChainResolver.class).to(PathMatchingFilterChainResolver.class).in(Singleton.class);
    bind(PathMatchingFilterChainResolver.class);
    expose(FilterChainResolver.class);

    // filter-chain manager
    bind(FilterChainManager.class).to(DefaultFilterChainManager.class);
    DefaultFilterChainManager filterChainManager = new DefaultFilterChainManager();
    customize(filterChainManager);
    bind(DefaultFilterChainManager.class).toInstance(filterChainManager);

    // realm-authenticator
    bind(Authenticator.class).to(FirstSuccessfulModularRealmAuthenticator.class).in(Singleton.class);
    bind(FirstSuccessfulModularRealmAuthenticator.class);
    expose(FirstSuccessfulModularRealmAuthenticator.class);

    // realms
    bindRealm().to(LocalRealm.class).in(Singleton.class);
  }

  @Override
  protected void bindSecurityManager(final AnnotatedBindingBuilder<? super SecurityManager> bind) {
    // required for some reason to prevent bootstrap "no implementation" errors
    bind.to(WebSecurityManager.class);

    WebSecurityManagerImpl securityManager = new WebSecurityManagerImpl(configuration);
    bind(WebSecurityManager.class).toInstance(securityManager);
    bind(WebSessionManagerImpl.class);
    expose(WebSecurityManager.class);

    bind(RealmSecurityManager.class).toInstance(securityManager);
    expose(RealmSecurityManager.class);
  }

  @Override
  protected void bindSessionManager(final AnnotatedBindingBuilder<SessionManager> bind) {
    // required for some reason to prevent bootstrap "no implementation" errors
    bind.to(WebSessionManager.class);

    WebSessionManagerImpl sessionManager = new WebSessionManagerImpl(configuration);
    bind(WebSessionManager.class).toInstance(sessionManager);
    bind(WebSessionManagerImpl.class);
    expose(WebSessionManager.class);

    bind(SessionDAO.class).to(MemorySessionDAO.class).in(Singleton.class);
    expose(SessionDAO.class);
  }

  /**
   * Customize filter-chain configuration.
   */
  protected void customize(final DefaultFilterChainManager manager) {
    // empty
  }

  //
  // Helpers
  //

  /**
   * Helper to construct and post-construct inject a filter instance.
   */
  protected <T extends Filter> T filter(final Class<T> type) {
    T instance;
    try {
      instance = type.newInstance();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    requestInjection(instance);
    return instance;
  }
}
