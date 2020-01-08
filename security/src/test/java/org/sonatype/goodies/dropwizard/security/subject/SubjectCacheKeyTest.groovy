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
package org.sonatype.goodies.dropwizard.security.subject

import org.apache.shiro.subject.Subject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * {@link SubjectCacheKey} tests.
 */
@RunWith(MockitoJUnitRunner.class)
class SubjectCacheKeyTest
{
  @Mock
  Subject subject

  @Test
  void 'key for authenticated'() {
    def principal = 'foo'
    Mockito.when(subject.getPrincipal()).thenReturn(principal)

    def key1 = SubjectCacheKey.of(subject)
    assert key1 != null
    assert key1.principal == principal
    assert !key1.anonymous

    def key2 = SubjectCacheKey.of(subject)
    assert key2 != null
    assert key2 == key1
    assert !key2.is(key1)
  }

  @Test
  void 'key for anonymous'() {
    def principal = null
    Mockito.when(subject.getPrincipal()).thenReturn(principal)

    def key1 = SubjectCacheKey.of(subject)
    assert key1 != null
    assert key1.principal == principal
    assert key1.anonymous

    def key2 = SubjectCacheKey.of(subject)
    assert key2 != null
    assert key2 == key1
    assert key2.is(key1)
  }
}
