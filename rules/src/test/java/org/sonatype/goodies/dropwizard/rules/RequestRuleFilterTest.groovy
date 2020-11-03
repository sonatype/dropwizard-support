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

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyZeroInteractions
import static org.mockito.Mockito.when

/**
 * {@link RequestRuleFilter} tests.
 */
@ExtendWith(MockitoExtension.class)
class RequestRuleFilterTest
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

  @BeforeEach
  void setUp() {
    underTest = new RequestRuleFilter(requestRuleService)
  }

  @Test
  void 'evaluated result is applied'() {
    def result = mock(RequestRuleResult.class)
    when(requestRuleService.evaluate(request)).thenReturn(result)

    underTest.filter(request, response, chain)

    verify(requestRuleService).evaluate(request)
    verify(result).apply(request, response, chain)
    verifyZeroInteractions(chain)
  }

  @Test
  void 'delegate to chain with no result'() {
    when(requestRuleService.evaluate(request)).thenReturn(null)

    underTest.filter(request, response, chain)

    verify(requestRuleService).evaluate(request)
    verify(chain).doFilter(request, response)
  }
}
