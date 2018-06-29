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
package org.apache.shiro.dropwizard;

import java.util.Collection;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// copied from: https://github.com/sonatype/nexus-internal/blob/e28b4254688e26ea6daae74b67ad85c3936db85e/components/nexus-security/src/main/java/org/sonatype/nexus/security/authc/FirstSuccessfulModularRealmAuthenticator.java

/**
 * {@link Authenticator} that will attempt to authenticate with each realm, stopping after it found a response.
 *
 * @see ModularRealmAuthenticator
 */
public class FirstSuccessfulModularRealmAuthenticator
    extends ModularRealmAuthenticator
{
  private static final Logger log = LoggerFactory.getLogger(FirstSuccessfulModularRealmAuthenticator.class);

  public FirstSuccessfulModularRealmAuthenticator() {
    log.debug("Created");
  }

  @Override
  public void setRealms(final Collection<Realm> realms) {
    log.debug("Realms: {}", realms);
    super.setRealms(realms);
  }

  @Override
  public void setAuthenticationStrategy(final AuthenticationStrategy authenticationStrategy) {
    log.debug("Authentication-strategy: {}", authenticationStrategy);
    super.setAuthenticationStrategy(authenticationStrategy);
  }

  @Override
  protected AuthenticationInfo doMultiRealmAuthentication(final Collection<Realm> realms,
                                                          final AuthenticationToken token)
  {
    log.trace("Iterating through [{}] realms for PAM authentication", realms.size());

    for (Realm realm : realms) {
      // check if the realm supports this token
      if (realm.supports(token)) {
        log.trace("Attempting to authenticate token [{}] using realm of type [{}]", token, realm);

        try {
          AuthenticationInfo info = realm.getAuthenticationInfo(token);
          if (info != null) {
            return info;
          }

          log.trace("Realm [{}] returned null when authenticating token [{}]", realm, token);
        }
        catch (Throwable t) {
          log.trace("Realm [{}] threw an exception during a multi-realm authentication attempt", realm, t);
        }
      }
      else {
        log.trace("Realm of type [{}] does not support token [{}]; skipping realm", realm, token);
      }
    }

    throw new AuthenticationException("Authentication token of type [" + token.getClass()
        + "] could not be authenticated by any configured realms.  Please ensure that at least one realm can "
        + "authenticate these tokens.");
  }
}
