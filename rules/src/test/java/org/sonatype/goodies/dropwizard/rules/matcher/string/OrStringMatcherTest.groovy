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

import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Test
import org.mockito.Mockito

import static org.junit.Assert.fail
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions
import static org.mockito.Mockito.when

/**
 * {@link OrStringMatcher} tests.
 */
class OrStringMatcherTest
  extends TestSupport
{
  @Test
  void 'at least one matcher required'() {
    try {
      //noinspection GroovyResultOfObjectAllocationIgnored
      new OrStringMatcher([])
      fail()
    }
    catch (IllegalStateException expected) {
      log expected
    }
  }

  @Test
  void 'match success'() {
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
    def matcher1 = Mockito.mock(StringMatcher.class)
    def matcher2 = Mockito.mock(StringMatcher.class)
    def underTest = new OrStringMatcher([ matcher1, matcher2 ])

    when(matcher1.matches(anyString())).thenReturn(false)
    when(matcher2.matches(anyString())).thenReturn(true)

    assert underTest.matches('foo')

    verify(matcher1, times(1)).matches('foo')
    verify(matcher2, times(1)).matches('foo')
    verifyNoMoreInteractions(matcher1, matcher2)
  }

  @Test
  void 'match order short-circuit'() {
    def matcher1 = Mockito.mock(StringMatcher.class)
    def matcher2 = Mockito.mock(StringMatcher.class)
    def underTest = new OrStringMatcher([ matcher1, matcher2 ])

    when(matcher1.matches(anyString())).thenReturn(true)
    when(matcher2.matches(anyString())).thenReturn(false)

    assert underTest.matches('foo')

    verify(matcher1, times(1)).matches('foo')
    verify(matcher2, times(0)).matches(anyString())
    verifyNoMoreInteractions(matcher1, matcher2)
  }
}
