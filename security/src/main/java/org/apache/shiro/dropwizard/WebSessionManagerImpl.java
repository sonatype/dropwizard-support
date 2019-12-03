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

import org.sonatype.goodies.dropwizard.security.SecurityConfiguration;

import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link WebSessionManager}.
 *
 * @since ???
 */
public class WebSessionManagerImpl
    extends DefaultWebSessionManager
{
  private static final Logger log = LoggerFactory.getLogger(WebSessionManagerImpl.class);

  public WebSessionManagerImpl(final SecurityConfiguration configuration) {
    checkNotNull(configuration);

    String cookieName = configuration.getSessionIdCookieName();
    getSessionIdCookie().setName(cookieName);
    log.info("Session-ID cookie-name: {}", cookieName);

    log.debug("Created");
  }
}
