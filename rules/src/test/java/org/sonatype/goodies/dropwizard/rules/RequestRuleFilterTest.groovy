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

import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyZeroInteractions
import static org.mockito.Mockito.when

/**
 * {@link RequestRuleFilter} tests.
 */
class RequestRuleFilterTest
  extends TestSupport
{
  @Mock
  private RequestRuleService requestRuleService

  @Mock
  private HttpServletRequest request

  @Mock
  private HttpServletResponse response

  @Mock
  private FilterChain chain

  private RequestRuleFilter underTest

  @Before
  void setUp() {
    underTest = new RequestRuleFilter(requestRuleService)
  }

  @Test
  void 'evaluated result is applied'() {
    def result = mock(RequestRuleResult.class)
    when(requestRuleService.evaluate(request)).thenReturn(result)

    underTest.filter(request, response, chain)

    verify(requestRuleService, times(1)).evaluate(request)
    verify(result, times(1)).apply(request, response, chain)
    verifyZeroInteractions(chain)
  }

  @Test
  void 'delegate to chain with no result'() {
    when(requestRuleService.evaluate(request)).thenReturn(null)

    underTest.filter(request, response, chain)

    verify(requestRuleService, times(1)).evaluate(request)
    verify(chain, times(1)).doFilter(request, response)
  }
}
