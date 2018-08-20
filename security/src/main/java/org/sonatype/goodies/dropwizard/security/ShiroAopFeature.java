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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.sonatype.goodies.dropwizard.util.GuiceEnhanced;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.authz.aop.AuthenticatedAnnotationHandler;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.authz.aop.GuestAnnotationHandler;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.authz.aop.RoleAnnotationHandler;
import org.apache.shiro.authz.aop.UserAnnotationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides AOP via Jersey mechanisms for Shiro annotations.
 *
 * @since ???
 */
public class ShiroAopFeature
    implements DynamicFeature
{
  private static final Logger log = LoggerFactory.getLogger(ShiroAopFeature.class);

  /**
   * Map of Shiro annotations to handlers.
   *
   * Handlers are all stateless and thread-safe.
   */
  private static final Map<Class<? extends Annotation>, AuthorizingAnnotationHandler> annotationHandlers = ImmutableMap.of(
      RequiresPermissions.class, new PermissionAnnotationHandler(),
      RequiresRoles.class, new RoleAnnotationHandler(),
      RequiresAuthentication.class, new AuthenticatedAnnotationHandler(),
      RequiresUser.class, new UserAnnotationHandler(),
      RequiresGuest.class, new GuestAnnotationHandler()
  );

  @Override
  public void configure(final ResourceInfo resourceInfo, final FeatureContext context) {
    checkNotNull(resourceInfo);
    checkNotNull(context);

    Class<?> resourceClass = resourceInfo.getResourceClass();
    Method resourceMethod = resourceInfo.getResourceMethod();

    // special handling for guice enhanced to resolve underlying class and re-resolve method
    if (GuiceEnhanced.isEnhanced(resourceClass)) {
      resourceClass = GuiceEnhanced.dereference(resourceClass);
      resourceMethod = GuiceEnhanced.dereference(resourceMethod);
    }

    log.trace("Detecting; class={}, method={}\n\n", resourceClass, resourceMethod);

    Multimap<AuthorizingAnnotationHandler,Annotation> handlers = HashMultimap.create();
    for (Entry<Class<? extends Annotation>, AuthorizingAnnotationHandler> entry : annotationHandlers.entrySet()) {
      Class<? extends Annotation> type = entry.getKey();
      AuthorizingAnnotationHandler handler = entry.getValue();

      Annotation anno = resourceClass.getAnnotation(type);
      if (anno != null) {
        handlers.put(handler, anno);
      }

      anno = resourceMethod.getAnnotation(type);
      if (anno != null) {
        handlers.put(handler, anno);
      }
    }

    if (!handlers.isEmpty()) {
      log.trace("Configuring: {} -> {}", resourceMethod, handlers);
      context.register(new SecurityFilter(handlers), Priorities.AUTHORIZATION);
    }
  }

  public static class SecurityFilter
      implements ContainerRequestFilter
  {
    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    // TODO: could optimize this down to an array of entries

    private final Multimap<AuthorizingAnnotationHandler,Annotation> handlers;

    public SecurityFilter(final Multimap<AuthorizingAnnotationHandler,Annotation> handlers) {
      this.handlers = checkNotNull(handlers);
    }

    @Override
    public void filter(final ContainerRequestContext context) throws IOException {
      log.trace("Filtering: {}", context);

      for (Entry<AuthorizingAnnotationHandler,Annotation> entry : handlers.entries()) {
        AuthorizingAnnotationHandler handler = entry.getKey();
        Annotation anno = entry.getValue();
        handler.assertAuthorized(anno);
      }
    }
  }
}
