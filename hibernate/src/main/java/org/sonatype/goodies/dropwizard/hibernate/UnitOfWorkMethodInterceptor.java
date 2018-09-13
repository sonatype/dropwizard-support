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

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.hibernate.UnitOfWorkAspect;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * {@link UnitOfWork} method-interceptor.
 *
 * @since ???
 * @see UnitOfWorkAspectFactory
 */
@Named
class UnitOfWorkMethodInterceptor
    implements MethodInterceptor
{
  private static final Logger log = LoggerFactory.getLogger(UnitOfWorkMethodInterceptor.class);

  @Nullable
  private UnitOfWorkAspectFactory aspectFactory;

  @Inject
  public void configure(final UnitOfWorkAspectFactory aspectFactory) {
    this.aspectFactory = checkNotNull(aspectFactory);
  }

  @Override
  public Object invoke(final MethodInvocation invocation) throws Throwable {
    checkState(aspectFactory != null, "Not configured");
    UnitOfWorkAspect aspect = aspectFactory.create();

    UnitOfWork uow = invocation.getMethod().getAnnotation(UnitOfWork.class);
    try {
      aspect.beforeStart(uow);
      Object result = invocation.proceed();
      aspect.afterEnd();
      return result;
    }
    catch (InvocationTargetException e) {
      aspect.onError();
      throw e.getCause();
    }
    catch (Exception e) {
      aspect.onError();
      throw e;
    }
    finally {
      aspect.onFinish();
    }
  }
}
