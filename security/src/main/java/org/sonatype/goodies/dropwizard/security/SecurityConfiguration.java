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
package org.sonatype.goodies.dropwizard.security;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.hibernate.validator.constraints.NotEmpty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Security configuration.
 *
 * @since ???
 */
@Beta
public class SecurityConfiguration
{
  @NotEmpty
  @JsonProperty
  private String sessionIdCookieName = ShiroHttpSession.DEFAULT_SESSION_ID_NAME;

  @Nonnull
  public String getSessionIdCookieName() {
    return sessionIdCookieName;
  }

  public void setSessionIdCookieName(@Nonnull final String sessionIdCookieName) {
    this.sessionIdCookieName = checkNotNull(sessionIdCookieName);
  }

  public SecurityConfiguration sessionIdCookieName(@Nonnull final String sessionCookieName) {
    setSessionIdCookieName(sessionCookieName);
    return this;
  }
}
