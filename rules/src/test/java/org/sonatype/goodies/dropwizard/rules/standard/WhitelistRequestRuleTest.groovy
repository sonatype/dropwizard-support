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
package org.sonatype.goodies.dropwizard.rules.standard

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.sonatype.goodies.dropwizard.rules.matcher.request.RemoteIpRequestMatcher
import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

/**
 * {@link WhitelistRequestRule} tests.
 */
class WhitelistRequestRuleTest
  extends TestSupport
{
  @Test
  void 'whitelist hit'() {
    def address = '1.2.3.4'
    def request = mock(HttpServletRequest.class)
    def response = mock(HttpServletResponse.class)
    def chain = mock(FilterChain.class)

    when(request.getRemoteAddr()).thenReturn(address)

    def underTest = new WhitelistRequestRule([ new RemoteIpRequestMatcher([ address ]) ])

    underTest.evaluate(request).with { result ->
      assert result != null
      result.apply(request, response, chain)
      verify(chain).doFilter(request, response)
    }
  }

  @Test
  void 'whitelist miss'() {
    def request = mock(HttpServletRequest.class)

    when(request.getRemoteAddr()).thenReturn('1.2.3.4')

    def underTest = new WhitelistRequestRule([ new RemoteIpRequestMatcher([ '6.6.6.0' ]) ])

    assert underTest.evaluate(request) == null
  }
}
