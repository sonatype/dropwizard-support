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
package org.sonatype.goodies.dropwizard.shiro;

import javax.inject.Singleton;
import javax.servlet.Filter;

import com.google.common.annotations.Beta;
import com.google.inject.binder.AnnotatedBindingBuilder;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.dropwizard.FirstSuccessfulModularRealmAuthenticator;
import org.apache.shiro.guice.ShiroModule;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;

// HACK: this is far from ideal, but for now just to get something a bit more common in place

/**
 * Support for security modules.
 *
 * @since 1.3.0
 */
@Beta
public class ShiroModuleSupport
    extends ShiroModule
{
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

    // realm-authorizer
    bind(Authorizer.class).to(ModularRealmAuthorizer.class).in(Singleton.class);
    bind(ModularRealmAuthorizer.class);
    expose(ModularRealmAuthorizer.class);
  }

  protected DefaultWebSecurityManager createSecurityManager() {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    securityManager.setRememberMeManager(null);
    return securityManager;
  }

  @Override
  protected void bindSecurityManager(final AnnotatedBindingBuilder<? super SecurityManager> bind) {
    // required for some reason to prevent bootstrap "no implementation" errors
    bind.to(WebSecurityManager.class);

    DefaultWebSecurityManager securityManager = createSecurityManager();
    bind(WebSecurityManager.class).toInstance(securityManager);
    expose(WebSecurityManager.class);

    bind(RealmSecurityManager.class).toInstance(securityManager);
    expose(RealmSecurityManager.class);
  }

  protected DefaultWebSessionManager createSessionManager() {
    return new DefaultWebSessionManager();
  }

  @Override
  protected void bindSessionManager(final AnnotatedBindingBuilder<SessionManager> bind) {
    // required for some reason to prevent bootstrap "no implementation" errors
    bind.to(WebSessionManager.class);

    DefaultWebSessionManager sessionManager = createSessionManager();
    bind(WebSessionManager.class).toInstance(sessionManager);
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

  protected void bindRealm(final Class<? extends Realm> type) {
    bind(type).in(Singleton.class);
    bindRealm().to(type);
  }

  /**
   * Helper to construct and post-construct inject a filter instance.
   */
  protected <T extends Filter> T filter(final Class<T> type) {
    T instance;
    try {
      instance = type.getDeclaredConstructor().newInstance();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    requestInjection(instance);
    return instance;
  }
}
