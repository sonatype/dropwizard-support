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
package org.sonatype.goodies.dropwizard.security.realms.local;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.apache.shiro.realm.Realm;

/**
 * {@link LocalRealm} module.
 *
 * @since 1.3.0
 */
public class LocalRealmModule
    extends AbstractModule
{
  @Override
  protected void configure() {
    bind(Realm.class).annotatedWith(Names.named(LocalRealm.NAME)).to(LocalRealm.class).in(Singleton.class);
  }
}
