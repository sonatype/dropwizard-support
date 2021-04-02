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
package org.sonatype.goodies.dropwizard.shiro.subject;

import javax.annotation.Nullable;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Subject} helper.
 *
 * @since 1.0.2
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
   *
   * {@literal null} return implies anonymous/guest.
   */
  @Nullable
  public static String getUsername(@Nullable final Subject subject) {
    if (subject != null) {
      // delegate to user-name extractor as various realms may need custom support to extract a user-name from principal
      return usernameExtractor.extract(subject);
    }
    return null;
  }

  /**
   * Extract user-name from current subject.
   *
   * {@code null} return implies anonymous/guest.
   */
  @Nullable
  public static String getUsername() {
    return getUsername(SecurityUtils.getSubject());
  }

  /**
   * Extract user-name from given principals.
   *
   * {@literal null} return implies anonymous/guest.
   */
  @Nullable
  public static String getUsername(@Nullable final PrincipalCollection principals) {
    if (principals != null) {
      return usernameExtractor.extract(principals);
    }
    return null;
  }

  private static volatile PrincipalUsernameExtractor usernameExtractor = new SimplePrincipalUsernameExtractor();

  /**
   * Return the current user-name extractor; never {@literal null}.
   *
   * @since 1.2.0
   */
  public static PrincipalUsernameExtractor getUsernameExtractor() {
    return usernameExtractor;
  }

  /**
   * Install a user-name extractor; never {@literal null}.
   *
   * @since 1.2.0
   */
  public static void setUsernameExtractor(final PrincipalUsernameExtractor extractor) {
    usernameExtractor = checkNotNull(extractor);
  }
}
