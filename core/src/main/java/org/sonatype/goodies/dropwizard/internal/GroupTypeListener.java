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
package org.sonatype.goodies.dropwizard.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.sonatype.goodies.dropwizard.Group;

import com.google.inject.Binder;
import org.eclipse.sisu.space.QualifiedTypeBinder;
import org.eclipse.sisu.space.QualifiedTypeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Group} type-listener.
 *
 * @since ???
 */
public class GroupTypeListener
    implements QualifiedTypeListener
{
  private static final Logger log = LoggerFactory.getLogger(GroupTypeListener.class);

  private final Set<String> enabled;

  private final QualifiedTypeBinder binder;

  public GroupTypeListener(final Binder binder, final Set<String> enabled) {
    checkNotNull(binder);
    this.binder = new QualifiedTypeBinder(binder);
    this.enabled = checkNotNull(enabled);
    log.info("Enabled groups: {}", enabled);
  }

  @Override
  public void hear(final Class<?> type, final Object source) {
    List<Group> groups = groupsOf(type);
    log.info("Hearing: {} -> {}", type, groups);
    boolean enable = false;

    // if no groups always enable
    if (groups.isEmpty()) {
      enable = true;
    }
    else {
      for (Group group : groups) {
        String name = group.value();
        if (Group.ALWAYS.equals(name) || enabled.contains(name)) {
          enable = true;
          log.info("Enabled group: {} -> {}", name, type);
          break;
        }
      }
    }

    if (enable) {
      binder.hear(type, source);
    }
  }

  /**
   * Returns all groups for given type.
   */
  private List<Group> groupsOf(final Class<?> type) {
    List<Group> result = new ArrayList<>();

    // include all groups from type
    Collections.addAll(result, type.getAnnotationsByType(Group.class));

    // include all groups from packages
    for (Package _package : packagesOf(type)) {
      Collections.addAll(result, _package.getAnnotationsByType(Group.class));
    }

    return result;
  }

  /**
   * Returns all packages (and parent-packages) of given type.
   */
  private static List<Package> packagesOf(final Class<?> type) {
    List<Package> result = new ArrayList<>();

    Package _package = type.getPackage();
    while (_package != null) {
      result.add(_package);

      // lookup parent of package
      String name = _package.getName();
      int i = name.lastIndexOf('.');
      if (i == -1) {
        break;
      }
      else {
        _package = Package.getPackage(name.substring(0, i));
      }
    }

    return result;
  }
}
