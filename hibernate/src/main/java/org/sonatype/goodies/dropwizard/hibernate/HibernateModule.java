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

import com.google.inject.AbstractModule;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.hibernate.SessionFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Hibernate module.
 *
 * @since 1.2.0
 */
public class HibernateModule
    extends AbstractModule
{
  private final HibernateBundle<?> bundle;

  public HibernateModule(final HibernateBundle<?> bundle) {
    this.bundle = checkNotNull(bundle);
  }

  @Override
  protected void configure() {
    bind(HibernateBundle.class).toInstance(bundle);
    bind(SessionFactory.class).toInstance(bundle.getSessionFactory());

    UnitOfWorkAwareProxyFactory proxyFactory = new UnitOfWorkAwareProxyFactory(bundle);
    bind(UnitOfWorkAwareProxyFactory.class).toInstance(proxyFactory);

    UnitOfWorkHelper helper = proxyFactory.create(UnitOfWorkHelper.class);
    bind(UnitOfWorkHelper.class).toInstance(helper);
  }
}
