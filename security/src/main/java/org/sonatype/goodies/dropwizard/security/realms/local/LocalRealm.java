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

import javax.inject.Inject;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.SimpleRole;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.shiro.util.PermissionUtils.resolvePermissions;

/**
 * Local {@link SimpleAccountRealm realm}.
 *
 * @since ???
 */
public class LocalRealm
    extends SimpleAccountRealm
{
  private static final Logger log = LoggerFactory.getLogger(LocalRealm.class);

  public static final String NAME = "local";

  private final LocalRealmConfiguration configuration;

  @Inject
  public LocalRealm(final LocalRealmConfiguration configuration) {
    this.configuration = checkNotNull(configuration);
    setName(NAME);
  }

  @Override
  protected void onInit() {
    super.onInit();

    for (LocalRole entry : configuration.getRoles()) {
      log.debug("Add role: {}", entry);

      // skip duplicate roles
      SimpleRole role = getRole(entry.getName());
      if (role != null) {
        log.warn("Duplicate role: {}", entry);
        continue;
      }

      role = new SimpleRole(entry.getName());
      role.setPermissions(resolvePermissions(entry.getPermissions(), getPermissionResolver()));

      add(role);
    }

    for (LocalUser entry : configuration.getUsers()) {
      log.debug("Add user: {}", entry);

      // skip duplicate users
      SimpleAccount account = getUser(entry.getName());
      if (account != null) {
        log.warn("Duplicate user: {}", entry);
        continue;
      }

      account = new SimpleAccount(entry.getName(), entry.getPassword(), NAME);

      for (String roleName : entry.getRoles()) {
        SimpleRole role = getRole(roleName);
        if (role != null) {
          account.addRole(role.getName());
          account.addObjectPermissions(role.getPermissions());
        }
      }

      account.addObjectPermissions(resolvePermissions(entry.getPermissions(), getPermissionResolver()));

      add(account);
    }
  }
}
