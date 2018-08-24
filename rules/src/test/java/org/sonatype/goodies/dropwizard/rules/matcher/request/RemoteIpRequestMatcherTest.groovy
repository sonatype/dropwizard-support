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

import org.sonatype.goodies.dropwizard.util.IpAddresses
import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions
import static org.mockito.Mockito.when

/**
 * {@link RemoteIpRequestMatcher} tests.
 */
class RemoteIpRequestMatcherTest
  extends TestSupport
{
  @Test
  void 'match hit'() {
    def request = mock(HttpServletRequest.class)
    when(request.getRemoteAddr()).thenReturn('1.2.3.4')

    def underTest = new RemoteIpRequestMatcher(new IpAddresses([ '1.2.3.4' ]))

    assert underTest.matches(request)

    verify(request, times(1)).getRemoteAddr()
    verifyNoMoreInteractions(request)
  }

  @Test
  void 'match miss'() {
    def request = mock(HttpServletRequest.class)
    when(request.getRemoteAddr()).thenReturn('1.2.3.4')

    def underTest = new RemoteIpRequestMatcher(new IpAddresses([ '5.6.7.8' ]))

    assert !underTest.matches(request)

    verify(request, times(1)).getRemoteAddr()
    verifyNoMoreInteractions(request)
  }
}
