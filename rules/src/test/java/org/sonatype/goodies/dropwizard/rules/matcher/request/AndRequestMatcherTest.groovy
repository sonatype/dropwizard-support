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
package org.sonatype.goodies.dropwizard.rules.matcher.request

import javax.servlet.http.HttpServletRequest

import org.sonatype.goodies.dropwizard.rules.matcher.string.ContainsStringMatcher

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.fail
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions
import static org.mockito.Mockito.when

/**
 * {@link AndRequestMatcher} tests.
 */
class AndRequestMatcherTest
{
  @SuppressWarnings("GroovyResultOfObjectAllocationIgnored")
  @Test
  void 'at least 2 matchers required'() {
    try {
      new AndRequestMatcher([])
      fail()
    }
    catch (IllegalStateException expected) {
      println expected
    }

    try {
      new AndRequestMatcher([ mock(RequestMatcher.class) ])
      fail()
    }
    catch (IllegalStateException expected) {
      println expected
    }

    new AndRequestMatcher([ mock(RequestMatcher.class), mock(RequestMatcher.class) ])
  }

  @Test
  void 'match hit'() {
    def request = mock(HttpServletRequest.class)
    when(request.getRequestURI()).thenReturn('/foo/bar/baz')

    def underTest = new AndRequestMatcher([
        new PathRequestMatcher(new ContainsStringMatcher('foo', false)),
        new PathRequestMatcher(new ContainsStringMatcher('baz', false))
    ])

    assert underTest.matches(request)

    verify(request, times(2)).getRequestURI()
    verifyNoMoreInteractions(request)
  }

  @Test
  void 'match order'() {
    def request = mock(HttpServletRequest.class)
    def matcher1 = mock(RequestMatcher.class)
    def matcher2 = mock(RequestMatcher.class)
    def underTest = new AndRequestMatcher([ matcher1, matcher2 ])

    when(matcher1.matches(request)).thenReturn(true)
    when(matcher2.matches(request)).thenReturn(true)

    assert underTest.matches(request)

    verify(matcher1).matches(request)
    verify(matcher2).matches(request)
    verifyNoMoreInteractions(matcher1, matcher2)
  }

  @Test
  void 'match order short-circuit'() {
    def request = mock(HttpServletRequest.class)
    def matcher1 = mock(RequestMatcher.class)
    def matcher2 = mock(RequestMatcher.class)
    def underTest = new AndRequestMatcher([ matcher1, matcher2 ])

    when(matcher1.matches(request)).thenReturn(false)
    when(matcher2.matches(request)).thenReturn(true)

    assert !underTest.matches(request)

    verify(matcher1).matches(request)
    verify(matcher2, never()).matches(request)
    verifyNoMoreInteractions(matcher1, matcher2)
  }
}
