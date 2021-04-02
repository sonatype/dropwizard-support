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

import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import org.apache.shiro.subject.Subject;

/**
 * Adapter around {@link Subject} for cache-key purposes.
 *
 * Special handling for anonymous/guest subjects.
 *
 * @since 1.0.2
 */
public final class SubjectCacheKey
{
  public static final SubjectCacheKey ANONYMOUS = new SubjectCacheKey(null);

  @Nullable
  private final Object principal;

  SubjectCacheKey(@Nullable final Subject subject) {
    this.principal = subject != null ? subject.getPrincipal() : null;
  }

  /**
   * Underlying subject principal; or null if anonymous/guest.
   */
  @Nullable
  public Object getPrincipal() {
    return principal;
  }

  public boolean isAnonymous() {
    return principal == null;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubjectCacheKey that = (SubjectCacheKey) o;
    return Objects.equals(principal, that.principal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(principal);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("principal", principal)
        .toString();
  }

  /**
   * Returns key for given subject.
   *
   * If the subject is anonymous/guest then {@link #ANONYMOUS} is returned.
   */
  public static SubjectCacheKey of(@Nullable final Subject subject) {
    if (SubjectHelper.isAnonymous(subject)) {
      return ANONYMOUS;
    }
    return new SubjectCacheKey(subject);
  }
}
