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
package org.sonatype.goodies.dropwizard.shiro.realms.local;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.NotEmpty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link LocalRealm} role.
 *
 * @since 1.3.0
 */
public class LocalUser
{
  @NotEmpty
  @JsonProperty
  private String name;

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull final String name) {
    this.name = checkNotNull(name);
  }

  @NotEmpty
  @JsonProperty
  private String password;

  @Nonnull
  public String getPassword() {
    return password;
  }

  public void setPassword(@Nonnull final String password) {
    this.password = checkNotNull(password);
  }

  @NotNull
  @JsonProperty
  private Set<String> roles = new LinkedHashSet<>();

  @Nonnull
  public Set<String> getRoles() {
    return roles;
  }

  public void setRoles(@Nonnull final Set<String> roles) {
    this.roles = checkNotNull(roles);
  }

  @NotNull
  @JsonProperty
  private Set<String> permissions = new LinkedHashSet<>();

  @Nonnull
  public Set<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(@Nonnull final Set<String> permissions) {
    this.permissions = checkNotNull(permissions);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("roles", roles)
        .add("permissions", permissions)
        .toString();
  }
}
