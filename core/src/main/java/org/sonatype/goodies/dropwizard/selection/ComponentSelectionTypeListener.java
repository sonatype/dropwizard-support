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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Binder;
import org.eclipse.sisu.space.QualifiedTypeBinder;
import org.eclipse.sisu.space.QualifiedTypeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Component selection type-listener.
 *
 * Allows configuration-based selection to refine which components will be enabled on discovery.
 *
 * @since ???
 */
public class ComponentSelectionTypeListener
    implements QualifiedTypeListener
{
  private static final Logger log = LoggerFactory.getLogger(ComponentSelectionTypeListener.class);

  private final ComponentSelectionConfiguration configuration;

  private final QualifiedTypeBinder binder;

  public ComponentSelectionTypeListener(final Binder binder,
                                        final ComponentSelectionConfiguration configuration) {
    checkNotNull(binder);
    this.binder = new QualifiedTypeBinder(binder);
    this.configuration = checkNotNull(configuration);
    log.info("{}", configuration);
  }

  @Override
  public void hear(final Class<?> type, final Object source) {
    if (isEnabled(type)) {
      binder.hear(type, source);
    }
  }

  /**
   * Check if given component type is enabled.
   */
  private boolean isEnabled(final Class<?> type) {
    // first check direct type name
    if (configuration.getTypes().contains(type.getCanonicalName())) {
      log.debug("Enabled by type-name: {}", type);
      return true;
    }

    // then explicit package name
    if (configuration.getPackages().contains(type.getPackage().getName())) {
      log.debug("Enabled by package-name: {}", type);
      return true;
    }

    // then wildcard package
    for (String name : configuration.getPackages()) {
      if (name.length() > 1 && name.endsWith("*")) {
        name = name.substring(0, name.length() - 1);
        if (type.getPackage().getName().startsWith(name)) {
          log.debug("Enabled by package-name wildcard: {}", type);
          return true;
        }
      }
    }

    // otherwise resolve group names from annotations
    Set<String> groups = groupsOf(type);
    log.trace("{} groups: {}", type, groups);

    // if no groups discovered, enable by default
    if (groups.isEmpty()) {
      log.debug("Enabled by default: {}", type);
      return true;
    }

    // else enable if a matching group is found
    for (String group : groups) {
      if (ComponentGroup.NEVER.equals(group)) {
        break;
      }
      if (ComponentGroup.ALWAYS.equals(group)) {
        log.debug("Enabled ALWAYS: {}", type);
        return true;
      }
      if (configuration.getGroups().contains(group)) {
        log.debug("Enabled by group-name {}: {}", group, type);
        return true;
      }
    }

    log.trace("Disabled: {}", type);
    return false;
  }

  /**
   * Returns all group-names for given type.
   */
  private Set<String> groupsOf(final Class<?> type) {
    Set<String> result = new HashSet<>();

    // include all groups from type
    for (ComponentGroup group : type.getAnnotationsByType(ComponentGroup.class)) {
      log.trace("{} group: {}", type, group);
      Collections.addAll(result, group.value());
    }

    // include all groups from package; only direct package not safe to look farther up due to how packages load
    Package _package = type.getPackage();
    for (ComponentGroup group : type.getPackage().getAnnotationsByType(ComponentGroup.class)) {
      log.trace("{} group: {}", _package, group);
      Collections.addAll(result, group.value());
    }

    return result;
  }
}
