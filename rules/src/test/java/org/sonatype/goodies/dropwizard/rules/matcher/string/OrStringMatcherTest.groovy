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
package org.sonatype.goodies.dropwizard.rules.matcher.string

import org.junit.Test

import static org.junit.Assert.fail
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions
import static org.mockito.Mockito.when

/**
 * {@link OrStringMatcher} tests.
 */
class OrStringMatcherTest
{
  @SuppressWarnings("GroovyResultOfObjectAllocationIgnored")
  @Test
  void 'at least 2 matchers required'() {
    try {
      new OrStringMatcher([])
      fail()
    }
    catch (IllegalStateException expected) {
      println expected
    }

    try {
      new OrStringMatcher([ mock(StringMatcher.class) ])
      fail()
    }
    catch (IllegalStateException expected) {
      println expected
    }

    new OrStringMatcher([ mock(StringMatcher.class), mock(StringMatcher.class) ])
  }

  @Test
  void 'match hit'() {
    def underTest = new OrStringMatcher([
        new ContainsStringMatcher('foo', false),
        new ContainsStringMatcher('qux', false)
    ])

    assert underTest.matches('foo bar baz')
    assert underTest.matches('a b c qux')
    assert !underTest.matches('a b c')
  }

  @Test
  void 'match order'() {
    def matcher1 = mock(StringMatcher.class)
    def matcher2 = mock(StringMatcher.class)
    def underTest = new OrStringMatcher([ matcher1, matcher2 ])

    when(matcher1.matches(anyString())).thenReturn(false)
    when(matcher2.matches(anyString())).thenReturn(true)

    assert underTest.matches('foo')

    verify(matcher1).matches('foo')
    verify(matcher2).matches('foo')
    verifyNoMoreInteractions(matcher1, matcher2)
  }

  @Test
  void 'match order short-circuit'() {
    def matcher1 = mock(StringMatcher.class)
    def matcher2 = mock(StringMatcher.class)
    def underTest = new OrStringMatcher([ matcher1, matcher2 ])

    when(matcher1.matches(anyString())).thenReturn(true)
    when(matcher2.matches(anyString())).thenReturn(false)

    assert underTest.matches('foo')

    verify(matcher1).matches('foo')
    verify(matcher2, never()).matches(anyString())
    verifyNoMoreInteractions(matcher1, matcher2)
  }
}
