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
import javax.ws.rs.HttpMethod

import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions
import static org.mockito.Mockito.when

/**
 * {@link MethodRequestMatcher} tests.
 */
class MethodRequestMatcherTest
  extends TestSupport
{
  @Test
  void 'match hit'() {
    def request = mock(HttpServletRequest.class)
    when(request.getMethod()).thenReturn(HttpMethod.GET)

    def underTest = new MethodRequestMatcher([ HttpMethod.GET ] as Set)

    assert underTest.matches(request)

    verify(request, times(1)).getMethod()
    verifyNoMoreInteractions(request)
  }

  @Test
  void 'match many'() {
    def request = mock(HttpServletRequest.class)
    when(request.getMethod()).thenReturn(HttpMethod.POST)

    def underTest = new MethodRequestMatcher([ HttpMethod.GET, HttpMethod.POST ] as Set)

    assert underTest.matches(request)

    verify(request, times(1)).getMethod()
    verifyNoMoreInteractions(request)
  }

  @Test
  void 'match case-insensitive'() {
    def request = mock(HttpServletRequest.class)
    when(request.getMethod()).thenReturn('gET')

    def underTest = new MethodRequestMatcher([ 'Get' ] as Set)

    assert underTest.matches(request)

    verify(request, times(1)).getMethod()
    verifyNoMoreInteractions(request)
  }
}
