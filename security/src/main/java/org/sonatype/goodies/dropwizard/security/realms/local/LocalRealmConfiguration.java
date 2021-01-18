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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link LocalRealm} configuration.
 *
 * @since 1.3.0
 */
public class LocalRealmConfiguration
{
  @Valid
  @NotNull
  @JsonProperty
  private List<LocalUser> users = new ArrayList<>();

  @Nonnull
  public List<LocalUser> getUsers() {
    return users;
  }

  public void setUsers(@Nonnull final List<LocalUser> users) {
    this.users = checkNotNull(users);
  }

  @Valid
  @NotNull
  @JsonProperty
  private List<LocalRole> roles = new ArrayList<>();

  @Nonnull
  public List<LocalRole> getRoles() {
    return roles;
  }

  public void setRoles(@Nonnull final List<LocalRole> roles) {
    this.roles = checkNotNull(roles);
  }
}
