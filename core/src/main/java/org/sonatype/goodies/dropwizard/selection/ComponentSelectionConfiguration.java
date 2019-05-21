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
package org.sonatype.goodies.dropwizard.selection;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link ComponentSelectionTypeListener} configuration.
 *
 * @since ???
 */
public class ComponentSelectionConfiguration
{
  /**
   * Optional set of component types to enable.
   */
  @NotNull
  @JsonProperty("types")
  private Set<String> types = new HashSet<>();

  /**
   * Optional set of component packages to enable.
   */
  @NotNull
  @JsonProperty("packages")
  private Set<String> packages = new HashSet<>();

  /**
   * Optional set of component groups to enable.
   */
  @NotNull
  @JsonProperty("groups")
  private Set<String> groups = new HashSet<>();

  @Nonnull
  public Set<String> getTypes() {
    return types;
  }

  public void setTypes(@Nonnull final Set<String> types) {
    this.types = checkNotNull(types);
  }

  @Nonnull
  public Set<String> getPackages() {
    return packages;
  }

  public void setPackages(@Nonnull final Set<String> packages) {
    this.packages = checkNotNull(packages);
  }

  @Nonnull
  public Set<String> getGroups() {
    return groups;
  }

  public void setGroups(@Nonnull final Set<String> groups) {
    this.groups = checkNotNull(groups);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("types", types)
        .add("packages", packages)
        .add("groups", groups)
        .toString();
  }
}
