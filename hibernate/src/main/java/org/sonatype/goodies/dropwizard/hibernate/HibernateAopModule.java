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
package org.sonatype.goodies.dropwizard.hibernate;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate AOP module.
 *
 * @since 1.2.0
 */
public class HibernateAopModule
    extends AbstractModule
{
  @Override
  protected void configure() {
    UnitOfWorkMethodInterceptor interceptor = new UnitOfWorkMethodInterceptor();
    bindInterceptor(
        Matchers.any(),
        new UowResourceMethodMatcher(),
        interceptor
    );
    requestInjection(interceptor);
  }

  /**
   * Matches non-JAX-RS resource-method annotated with {@link UnitOfWork}.
   *
   * Need to skip JAX-RS resource-methods, as {@link io.dropwizard.hibernate.UnitOfWorkApplicationListener}
   * already covers this AOP use-case; and presently can not be easily disabled
   * (would require rewriting {@link io.dropwizard.hibernate.HibernateBundle}.
   */
  private static class UowResourceMethodMatcher
      extends AbstractMatcher<AnnotatedElement>
      implements Serializable
  {
    private static final Logger log = LoggerFactory.getLogger(UowResourceMethodMatcher.class);

    final Set<Class<? extends Annotation>> resourceAnnotationTypes = ImmutableSet.of(
        DELETE.class,
        GET.class,
        HEAD.class,
        OPTIONS.class,
        POST.class,
        PUT.class
    );

    @Override
    public boolean matches(final AnnotatedElement element) {
      boolean matched = !isResourceMethod(element) && hasAnnotation(element, UnitOfWork.class);
      if (matched) {
        log.trace("Matched: {}", element);
      }
      return matched;
    }

    private boolean isResourceMethod(final AnnotatedElement element) {
      for (Class<? extends Annotation> type : resourceAnnotationTypes) {
        if (hasAnnotation(element, type)) {
          return true;
        }
      }
      return false;
    }

    private boolean hasAnnotation(final AnnotatedElement element, final Class<? extends Annotation> type) {
      Annotation present = element.getAnnotation(type);
      return present != null;
    }
  }
}
