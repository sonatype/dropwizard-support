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
package org.sonatype.goodies.dropwizard.rules

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.sonatype.goodies.dropwizard.rules.matcher.request.RemoteIpRequestMatcher
import org.sonatype.goodies.dropwizard.rules.standard.BlacklistRequestRule
import org.sonatype.goodies.dropwizard.rules.standard.WhitelistRequestRule
import org.sonatype.goodies.testsupport.TestSupport

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyZeroInteractions
import static org.mockito.Mockito.when

/**
 * {@link RequestRuleServiceImpl} tests.
 */
class RequestRuleServiceImplTest
  extends TestSupport
{
  @Mock
  private HttpServletRequest request

  @Mock
  private HttpServletResponse response

  @Mock
  private FilterChain chain

  @Mock
  private RequestRule rule1

  @Mock
  private RequestRule rule2

  @Mock
  private RequestRule rule3

  private RequestRuleServiceImpl underTest

  @Before
  void setUp() {
    def config = new RequestRuleConfiguration(
        rules: [
            rule1,
            new WhitelistRequestRule([
                new RemoteIpRequestMatcher([ '1.2.3.4' ])
            ]),
            rule2,
            new BlacklistRequestRule([
                new RemoteIpRequestMatcher([ '6.6.6.0' ])
            ], null, null),
            rule3
        ]
    )
    underTest = new RequestRuleServiceImpl(config)
    underTest.start()
  }

  @After
  void tearDown() {
    underTest.stop()
  }

  @Test
  void 'whitelisted request'() {
    when(request.getRemoteAddr()).thenReturn('1.2.3.4')

    def result = underTest.evaluate(request)
    assert result != null

    result.apply(request, response, chain)

    verify(chain).doFilter(request, response)
    verify(rule1).evaluate(request)
    verifyZeroInteractions(rule2)
    verifyZeroInteractions(rule3)
  }

  @Test
  void 'blacklisted request'() {
    when(request.getRemoteAddr()).thenReturn('6.6.6.0')

    def result = underTest.evaluate(request)
    assert result != null
    result.apply(request, response, chain)

    verify(response).sendError(BlacklistRequestRule.DEFAULT_STATUS.statusCode, BlacklistRequestRule.DEFAULT_REASON)
    verify(rule1).evaluate(request)
    verify(rule2).evaluate(request)
    verifyZeroInteractions(rule3)
  }

  @Test
  void 'nothing matched'() {
    when(request.getRemoteAddr()).thenReturn('8.8.8.8')

    def result = underTest.evaluate(request)
    assert result == null

    verify(rule1).evaluate(request)
    verify(rule2).evaluate(request)
    verify(rule3).evaluate(request)
  }
}
