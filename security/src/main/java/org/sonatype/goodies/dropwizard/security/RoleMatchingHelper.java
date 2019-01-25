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

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import org.apache.shiro.subject.Subject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to match roles for subject.
 *
 * @since ???
 */
public class RoleMatchingHelper
{
  /**
   * Bulk request roles for subject, and pick first matching role.
   *
   * Implies that given roles collection is ordered.
   */
  @Nullable
  public static String matchFirst(final Subject subject, final Collection<String> roles) {
    checkNotNull(subject);
    checkNotNull(roles);

    List<String> names = ImmutableList.copyOf(roles);
    boolean[] matches = subject.hasRoles(names);
    for (int i=0; i<matches.length; i++) {
      if (matches[i]) {
        return names.get(i);
      }
    }

    return null;
  }
}
