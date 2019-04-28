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

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.HttpMethod

import org.sonatype.goodies.dropwizard.rules.matcher.request.MethodRequestMatcher
import org.sonatype.goodies.dropwizard.rules.matcher.request.RemoteIpRequestMatcher
import org.sonatype.goodies.dropwizard.rules.matcher.request.RequestMatcher

import com.codahale.metrics.MetricRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

import static org.mockito.Mockito.mock

/**
 * {@link MatchRequestRule} tests.
 */
@RunWith(MockitoJUnitRunner.class)
class MatchRequestRuleTest
{
  @Mock
  HttpServletRequest request

  @Mock
  RequestRuleResult ruleResult

  @Test
  void 'get matcher by type'() {
    def matchers = [
        new MethodRequestMatcher(HttpMethod.GET),
        new MethodRequestMatcher(HttpMethod.POST)
    ]
    def underTest = new MatchRequestRule('test', matchers) {
      @Override
      protected RequestRuleResult matched(final RequestMatcher matcher, final HttpServletRequest request) {
        return null
      }
    }

    underTest.getMatcher(RemoteIpRequestMatcher.class).with {
      assert it == null
    }

    underTest.getMatcher(MethodRequestMatcher.class).with {
      assert it != null
      assert it.methods.contains(HttpMethod.GET)
    }
  }

  @Test
  void 'get matchers by type'() {
    def matchers = [
        new MethodRequestMatcher(HttpMethod.GET),
        new MethodRequestMatcher(HttpMethod.POST)
    ]
    def underTest = new MatchRequestRule('test', matchers) {
      @Override
      protected RequestRuleResult matched(final RequestMatcher matcher, final HttpServletRequest request) {
        return null
      }
    }

    underTest.getMatchers(RemoteIpRequestMatcher.class).with {
      assert it != null
      assert it.isEmpty()
    }

    underTest.getMatchers(MethodRequestMatcher.class).with {
      assert it != null
      assert it.size() == 2
    }

    underTest.getMatchers().with {
      assert it != null
      assert it.size() == 2
    }
  }

  private static RequestMatcher createMatcher(final boolean matched) {
    return new RequestMatcher() {
      @Override
      boolean matches(final HttpServletRequest request) {
        return matched
      }
    }
  }

  @Test
  void 'matched request ticks meter'() {
    def metrics = new MetricRegistry()
    MatchRequestRule underTest = new MatchRequestRule('test', [ createMatcher(true) ]) {
      @Override
      protected RequestRuleResult matched(final RequestMatcher matcher, final HttpServletRequest request) {
        return ruleResult
      }
    }
    underTest.metric = 'test.match'
    underTest.configure(metrics)

    def metric = metrics.meter('test.match')
    assert metric.count == 0

    underTest.evaluate(request)

    assert metric.count == 1
  }

  @Test
  void 'non-matched request does not tick meter'() {
    def metrics = new MetricRegistry()
    MatchRequestRule underTest = new MatchRequestRule('test', [ createMatcher(false) ]) {
      @Override
      protected RequestRuleResult matched(final RequestMatcher matcher, final HttpServletRequest request) {
        return null
      }
    }
    underTest.metric = 'test.match'
    underTest.configure(metrics)

    def metric = metrics.meter('test.match')
    assert metric.count == 0

    def request = mock(HttpServletRequest.class)
    underTest.evaluate(request)

    assert metric.count == 0
  }
}
