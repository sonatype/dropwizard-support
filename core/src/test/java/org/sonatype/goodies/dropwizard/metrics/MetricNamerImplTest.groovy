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
package org.sonatype.goodies.dropwizard.metrics

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.sonatype.goodies.dropwizard.metrics.MetricNamerImpl.CLASS
import static org.sonatype.goodies.dropwizard.metrics.MetricNamerImpl.METHOD
import static org.sonatype.goodies.dropwizard.metrics.MetricNamerImpl.NAME
import static org.sonatype.goodies.dropwizard.metrics.MetricNamerImpl.SIMPLE_CLASS

/**
 * {@link MetricNamerImpl} tests.
 */
class MetricNamerImplTest
{
  MetricNamerImpl underTest

  @BeforeEach
  void setUp() {
    underTest = new MetricNamerImpl()
  }

  @MetricNameFormat("example.#class.#method")
  private static class Example1
  {
    void test1() {
      // empty
    }
  }

  @Test
  void 'metric name from format'() {
    underTest.metricName(Example1.class.getDeclaredMethod('test1'), '').with {
      assert it == "example.${Example1.class.name}.test1"
    }
  }

  @Test
  void 'apply format'() {
    def method = Example1.class.getDeclaredMethod('test1')
    def type = method.declaringClass

    underTest.applyFormat(method, '', type, "test.${CLASS}.${METHOD}").with {
      assert it == "test.${type.name}.test1"
    }

    underTest.applyFormat(method, '', type, "test.${SIMPLE_CLASS}.${METHOD}").with {
      assert it == "test.${type.simpleName}.test1"
    }

    underTest.applyFormat(method, 'foo', type, "test.${SIMPLE_CLASS}.${NAME}").with {
      assert it == "test.${type.simpleName}.foo"
    }
  }
}
