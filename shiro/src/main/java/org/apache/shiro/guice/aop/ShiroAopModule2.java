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
package org.apache.shiro.guice.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.sonatype.goodies.dropwizard.jaxrs.Resource;
import org.sonatype.goodies.dropwizard.security.ShiroAopFeature;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import org.apache.shiro.aop.AnnotationMethodInterceptor;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.DefaultAnnotationResolver;
import org.apache.shiro.authz.aop.AuthenticatedAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.GuestAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.PermissionAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.RoleAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.UserAnnotationMethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Shiro AOP Guice module.
 *
 * Adds additional logic to cope with JAX-RS {@link Resource} implementations which have security AOP handled via
 * {@link ShiroAopFeature}.
 *
 * @since 1.0.2
 */
public class ShiroAopModule2
    extends AbstractModule
{
  private static final Logger log = LoggerFactory.getLogger(ShiroAopModule2.class);

  @Override
  protected void configure() {
    AnnotationResolver resolver = new DefaultAnnotationResolver();
    bindShiroInterceptor(new RoleAnnotationMethodInterceptor(resolver));
    bindShiroInterceptor(new PermissionAnnotationMethodInterceptor(resolver));
    bindShiroInterceptor(new AuthenticatedAnnotationMethodInterceptor(resolver));
    bindShiroInterceptor(new UserAnnotationMethodInterceptor(resolver));
    bindShiroInterceptor(new GuestAnnotationMethodInterceptor(resolver));
  }

  /**
   * Bind interceptors around methods which have Shiro annotations and
   * are *not* JAX-RS {@link Resource} implementations.
   */
  private void bindShiroInterceptor(final AnnotationMethodInterceptor interceptor) {
    bindInterceptor(Matchers.any(), new AbstractMatcher<Method>()
    {
      public boolean matches(final Method method) {
        Class<? extends Annotation> atype = interceptor.getHandler().getAnnotationClass();
        Class<?> dtype = method.getDeclaringClass();
        boolean resource = Resource.class.isAssignableFrom(dtype);
        boolean match = !resource && (method.getAnnotation(atype) != null || dtype.getAnnotation(atype) != null);
        if (match) {
          log.info("Configuring: {} -> {}", method, atype);
        }
        return match;
      }
    }, new AopAllianceMethodInterceptorAdapter(interceptor));
  }
}
