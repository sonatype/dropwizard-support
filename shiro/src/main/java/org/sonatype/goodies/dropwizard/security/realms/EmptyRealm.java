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
package org.sonatype.goodies.dropwizard.security.realms;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.Realm;

/**
 * Empty {@link Realm} - only used to satisfy Shiro's need for an initial realm binding.
 *
 * @since 1.0.0
 */
public final class EmptyRealm
    implements Realm
{
  public static final String NAME = "empty";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean supports(final AuthenticationToken token) {
    return false;
  }

  @Override
  public AuthenticationInfo getAuthenticationInfo(final AuthenticationToken token) {
    return null;
  }
}
