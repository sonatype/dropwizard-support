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

import javax.annotation.Nullable;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * {@link Subject} helper.
 *
 * @since ???
 */
public final class SubjectHelper
{
  private SubjectHelper() {
    // empty
  }

  /**
   * Check if given subject is anonymous/guest.
   */
  public static boolean isAnonymous(@Nullable final Subject subject) {
    return subject == null || subject.getPrincipal() == null;
  }

  /**
   * Check if current subject is anonymous/guest.
   */
  public static boolean isAnonymous() {
    return isAnonymous(SecurityUtils.getSubject());
  }

  /**
   * Check if given subject is authenticated.
   */
  public static boolean isAuthenticated(@Nullable final Subject subject) {
    return !isAnonymous(subject);
  }

  /**
   * Check if current subject is authenticated.
   */
  public static boolean isAuthenticated() {
    return isAuthenticated(SecurityUtils.getSubject());
  }

  /**
   * Extract user-name from given subject.
   */
  @Nullable
  public static String getUsername(@Nullable final Subject subject) {
    if (subject != null) {
      Object principal = subject.getPrincipal();
      if (principal != null) {
        return principal.toString();
      }
    }
    return null;
  }

  /**
   * Extract user-name from current subject.
   */
  @Nullable
  public static String getUsername() {
    return getUsername(SecurityUtils.getSubject());
  }
}
