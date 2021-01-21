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
package org.sonatype.goodies.dropwizard.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.spi.FilterReply
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * {@link LoggerNameFilter} tests.
 */
class LoggerNameFilterTest
{
  @Test
  void 'filter includes desired loggers'() {
    def underTest = new LoggerNameFilter.Factory(
        include: [
            'foo.bar',
            'a.b.c'
        ]
    ).build()

    def event = { String loggerName ->
      def result = mock(ILoggingEvent.class)
      when(result.getLoggerName()).thenReturn(loggerName)
      return result
    }

    def data = [
        'foo.bar': FilterReply.ACCEPT,
        'foo.bar.baz': FilterReply.ACCEPT,
        'foo': FilterReply.DENY,
        'foo.qux': FilterReply.DENY,
        'a.b.c': FilterReply.ACCEPT,
        'a.b.c.d': FilterReply.ACCEPT,
        'a.b': FilterReply.DENY,
        'e.f.g': FilterReply.DENY
    ]

    data.each {loggerName, result ->
      underTest.decide(event(loggerName)) == result
    }
  }
}
