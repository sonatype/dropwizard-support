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
package org.sonatype.goodies.dropwizard.shiro.authz

import org.apache.shiro.subject.Subject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.mockito.Mockito.when

/**
 * {@link RoleMatchingHelper} tests.
 */
@ExtendWith(MockitoExtension.class)
class RoleMatchingHelperTest
{
  @Mock
  Subject subject

  @Test
  void 'matching role'() {
    def roles = [
        'foo',
        'bar',
        'baz'
    ]
    def result = [
        false,
        true, // simulates subject only having 'bar' role
        false
    ] as boolean[]

    when(subject.hasRoles(roles)).thenReturn(result)

    def selected = RoleMatchingHelper.matchFirst(subject, roles)
    assert selected == 'bar'
  }

  @Test
  void 'non-matching role'() {
    def roles = [
        'foo',
        'bar',
        'baz'
    ]
    def result = [
        false,
        false,
        false
    ] as boolean[]

    when(subject.hasRoles(roles)).thenReturn(result)

    def selected = RoleMatchingHelper.matchFirst(subject, roles)
    assert selected == null
  }
}
