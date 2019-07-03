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
package org.sonatype.goodies.dropwizard.jdbi;

import javax.inject.Provider;

import org.jdbi.v3.core.Jdbi;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to provide JDBI <em>on-demand</em> DAO instances.
 *
 * @since 1.2.0
 */
public class OnDemandDaoProviderSupport<T>
    implements Provider<T>
{
  private final Jdbi database;

  private final Class<T> type;

  public OnDemandDaoProviderSupport(final Jdbi database, final Class<T> type) {
    this.database = checkNotNull(database);
    checkArgument(type.isInterface(), "Type not interface: %s", type);
    this.type = checkNotNull(type);
  }

  @Override
  public T get() {
    return database.onDemand(type);
  }
}