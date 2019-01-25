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
package org.sonatype.goodies.dropwizard.ratelimit

import org.apache.shiro.subject.Subject
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.util.ThreadContext

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Security helper for testing.
 */
class SecurityHelper
{
  static void setup() {
    reset()
    ThreadContext.bind(mock(SecurityManager.class))
  }

  static void reset() {
    ThreadContext.unbindSubject()
    ThreadContext.unbindSecurityManager()
  }

  static Subject subject(final Object principal) {
    Subject subject = mock(Subject.class)
    when(subject.getPrincipal()).thenReturn(principal)
    return subject
  }

  static Subject bindSubject(final Object principal) {
    def subject = subject(principal)
    ThreadContext.bind(subject)
    return subject
  }
}
