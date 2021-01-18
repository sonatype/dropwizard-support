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
package org.sonatype.goodies.dropwizard.ratelimit

import javax.servlet.http.HttpServletRequest

import com.codahale.metrics.MetricRegistry
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Tests for {@link RatelimitServiceImpl}.
 */
class RatelimitServiceImplTest
{
  private RatelimitServiceImpl underTest

  @BeforeEach
  void setUp() {
    underTest = new RatelimitServiceImpl(new RatelimitConfiguration(), new MetricRegistry())
    SecurityHelper.setup()
  }

  @AfterEach
  void tearDown() {
    SecurityHelper.reset()
  }

  @Test
  void 'identify request from username'() {
    SecurityHelper.bindSubject('test')

    def request = mock(HttpServletRequest.class)
    when(request.getRemoteAddr()).thenReturn('1.2.3.4')

    def id = underTest.identify(request)
    println id

    assert id != null
    assert id.type == RatelimitTracker.Identifier.Type.USERNAME
    assert id.value == 'test'
  }

  @Test
  void 'identify request from remote-ip'() {
    def request = mock(HttpServletRequest.class)
    when(request.getRemoteAddr()).thenReturn('1.2.3.4')

    def id = underTest.identify(request)
    println id

    assert id != null
    assert id.type == RatelimitTracker.Identifier.Type.REMOTE_IP
    assert id.value == '1.2.3.4'
  }
}
