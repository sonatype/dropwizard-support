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

import static javax.ws.rs.core.Response.Status.BAD_REQUEST
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.sonatype.goodies.dropwizard.rules.standard.BlacklistRequestRule.DEFAULT_REASON
import static org.sonatype.goodies.dropwizard.rules.standard.BlacklistRequestRule.DEFAULT_STATUS

/**
 * {@link BlacklistRequestRule} tests.
 */
class BlacklistRequestRuleTest
  extends TestSupport
{
  @Test
  void 'custom reason'() {
    def reason = 'foo bar'
    def address = '1.2.3.4'
    def request = mock(HttpServletRequest.class)
    def response = mock(HttpServletResponse.class)
    def chain = mock(FilterChain.class)

    when(request.getRemoteAddr()).thenReturn(address)

    def underTest = new BlacklistRequestRule([ new RemoteIpRequestMatcher([ address ]) ], null, reason)

    underTest.evaluate(request).with { result ->
      assert result != null
      result.apply(request, response, chain)
      verify(response).sendError(DEFAULT_STATUS.statusCode, reason)
    }
  }

  @Test
  void 'custom status'() {
    def address = '1.2.3.4'
    def request = mock(HttpServletRequest.class)
    def response = mock(HttpServletResponse.class)
    def chain = mock(FilterChain.class)

    when(request.getRemoteAddr()).thenReturn(address)

    def underTest = new BlacklistRequestRule([ new RemoteIpRequestMatcher([ address ]) ], BAD_REQUEST, null)

    underTest.evaluate(request).with { result ->
      assert result != null
      result.apply(request, response, chain)
      verify(response).sendError(BAD_REQUEST.statusCode, DEFAULT_REASON)
    }
  }

  @Test
  void 'not blacklisted'() {
    def request = mock(HttpServletRequest.class)

    when(request.getRemoteAddr()).thenReturn('1.2.3.4')

    def underTest = new BlacklistRequestRule([ new RemoteIpRequestMatcher([ '6.6.6.0' ]) ], null, null)

    assert underTest.evaluate(request) == null
  }
}
