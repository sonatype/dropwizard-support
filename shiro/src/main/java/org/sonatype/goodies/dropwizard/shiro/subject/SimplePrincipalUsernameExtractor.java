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

import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Simple {@link PrincipalUsernameExtractor}.
 *
 * @since 1.2.0
 */
public class SimplePrincipalUsernameExtractor
    implements PrincipalUsernameExtractor
{
  /**
   * @since 1.3.0
   */
  @Nullable
  @Override
  public String extract(final PrincipalCollection principals) {
    checkNotNull(principals);
    Object principal = principals.getPrimaryPrincipal();
    return extract(principal);
  }

  @Nullable
  @Override
  public String extract(final Subject subject) {
    checkNotNull(subject);
    Object principal = subject.getPrincipal();
    return extract(principal);
  }

  private String extract(@Nullable final Object principal) {
    if (principal != null) {
      return principal.toString();
    }
    return null;
  }
}
