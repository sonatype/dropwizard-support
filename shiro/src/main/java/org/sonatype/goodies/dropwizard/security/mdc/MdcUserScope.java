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
package org.sonatype.goodies.dropwizard.security.mdc;

import javax.annotation.Nullable;

import org.sonatype.goodies.dropwizard.security.subject.SubjectHelper;
import org.sonatype.goodies.dropwizard.security.subject.SystemSubject;

import org.apache.shiro.subject.Subject;
import org.slf4j.MDC;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * MDC user scope helper.
 *
 * @since 1.0.0
 */
public final class MdcUserScope
  implements AutoCloseable
{
  public static final String KEY = "user";

  public static final String SYSTEM = SystemSubject.PRINCIPAL;

  public static final String ANONYMOUS = "*UNKNOWN";

  private MdcUserScope(final String user) {
    checkNotNull(user);
    MDC.put(KEY, user);
  }

  /**
   * @see #reset()
   */
  @Override
  public void close() {
    reset();
  }

  /**
   * Reset user scope.
   */
  public static void reset() {
    MDC.remove(KEY);
  }

  /**
   * Return scope for given user.
   */
  public static MdcUserScope forUser(final String user) {
    return new MdcUserScope(user);
  }

  /**
   * Return scope for system.
   */
  public static MdcUserScope forSystem() {
    return forUser(SYSTEM);
  }

  /**
   * Return scope for anonymous.
   */
  public static MdcUserScope forAnonymous() {
    return forUser(ANONYMOUS);
  }

  /**
   * Return scope for subject.
   */
  public static MdcUserScope forSubject(@Nullable final Subject subject) {
    String user = SubjectHelper.getUsername(subject);
    if (user != null) {
      return forUser(user);
    }
    return forAnonymous();
  }
}
