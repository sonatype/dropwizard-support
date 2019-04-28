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

import org.junit.Test

import static javax.ws.rs.core.HttpHeaders.USER_AGENT
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions
import static org.mockito.Mockito.when

/**
 * {@link HeaderRequestMatcher} tests.
 */
class HeaderRequestMatcherTest
{
  @Test
  void 'match hit'() {
    def request = mock(HttpServletRequest.class)
    when(request.getHeader(USER_AGENT)).thenReturn('foo/1')

    def underTest = new HeaderRequestMatcher(USER_AGENT, new ContainsStringMatcher('foo', false))

    assert underTest.matches(request)

    verify(request).getHeader(USER_AGENT)
    verifyNoMoreInteractions(request)
  }

  @Test
  void 'match miss'() {
    def request = mock(HttpServletRequest.class)
    when(request.getHeader(USER_AGENT)).thenReturn('bar/1')

    def underTest = new HeaderRequestMatcher(USER_AGENT, new ContainsStringMatcher('foo', false))

    assert !underTest.matches(request)

    verify(request).getHeader(USER_AGENT)
    verifyNoMoreInteractions(request)
  }

  @Test
  void 'match miss when value null'() {
    def request = mock(HttpServletRequest.class)
    when(request.getHeader(USER_AGENT)).thenReturn(null)

    def underTest = new HeaderRequestMatcher(USER_AGENT, new ContainsStringMatcher('foo', false))

    assert !underTest.matches(request)

    verify(request).getHeader(USER_AGENT)
    verifyNoMoreInteractions(request)
  }
}
