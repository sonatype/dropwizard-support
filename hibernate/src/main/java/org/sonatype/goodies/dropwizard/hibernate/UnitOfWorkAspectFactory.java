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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.dropwizard.hibernate.UnitOfWorkAspect;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link UnitOfWorkAspect} factory.
 *
 * @since 1.2.0
 * @see UnitOfWorkAwareProxyFactory#newAspect()
 */
@Named
@Singleton
public class UnitOfWorkAspectFactory
{
  private static final Logger log = LoggerFactory.getLogger(UnitOfWorkAspectFactory.class);

  private final UnitOfWorkAwareProxyFactory proxyFactory;

  @Inject
  public UnitOfWorkAspectFactory(final UnitOfWorkAwareProxyFactory proxyFactory) {
    this.proxyFactory = checkNotNull(proxyFactory);
  }

  public UnitOfWorkAspect create() {
    return proxyFactory.newAspect();
  }
}
