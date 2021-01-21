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

import org.sonatype.goodies.dropwizard.logging.MessageBlacklistFilter.Operation

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.spi.FilterReply
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * {@link MessageBlacklistFilter} tests.
 */
class MessageBlacklistFilterTest
{
  private static ILoggingEvent event(final String loggerName, final Level level, final String message) {
    def event = mock(ILoggingEvent.class)
    when(event.getLoggerName()).thenReturn(loggerName)
    when(event.getLevel()).thenReturn(level)
    when(event.getMessage()).thenReturn(message)
    return event
  }

  @Test
  void 'filter with logger'() {
    def underTest = new MessageBlacklistFilter.Factory(
        logger: 'foo.bar.baz',
        op: Operation.EQUALS,
        value: 'test'
    ).build()

    def data = [
        'foo.bar.baz': FilterReply.DENY,
        'foo.bar': FilterReply.NEUTRAL,
        'foo.bar.baz.qux': FilterReply.DENY,
        'a.b.c': FilterReply.NEUTRAL,
    ]
    data.each {loggerName, result ->
      underTest.decide(event(loggerName, Level.INFO, 'test')) == result
    }
  }

  @Test
  void 'filter with level'() {
    def underTest = new MessageBlacklistFilter.Factory(
        level: Level.INFO,
        op: Operation.EQUALS,
        value: 'test'
    ).build()

    def data = [
        (Level.DEBUG): FilterReply.NEUTRAL,
        (Level.INFO): FilterReply.DENY,
        (Level.WARN): FilterReply.DENY
    ]
    data.each {level, result ->
      underTest.decide(event('foo.bar.baz', level, 'test')) == result
    }
  }

  @Test
  void 'filter with logger and level'() {
    def underTest = new MessageBlacklistFilter.Factory(
        logger: 'foo.bar.baz',
        level: Level.INFO,
        op: Operation.EQUALS,
        value: 'test'
    ).build()

    def data = [
        ([logger: 'foo.bar.baz', level: Level.DEBUG]): FilterReply.NEUTRAL,
        ([logger: 'foo.bar.baz', level: Level.INFO]): FilterReply.DENY,
        ([logger: 'foo.bar.baz', level: Level.WARN]): FilterReply.DENY,
        ([logger: 'a.b.c', level: Level.DEBUG]): FilterReply.NEUTRAL,
        ([logger: 'a.b.c', level: Level.INFO]): FilterReply.NEUTRAL,
        ([logger: 'a.b.c', level: Level.WARN]): FilterReply.NEUTRAL
    ]
    data.each {config, result ->
      //noinspection GroovyAssignabilityCheck
      underTest.decide(event(config.logger, config.level, 'test')) == result
    }
  }

  @Test
  void 'filter operation starts-with'() {
    def underTest = new MessageBlacklistFilter.Factory(
        logger: 'foo.bar.baz',
        level: Level.INFO,
        op: Operation.STARTS_WITH,
        value: 'test'
    ).build()

    def data = [
        'not test': FilterReply.NEUTRAL,
        'test': FilterReply.DENY,
        'testing': FilterReply.DENY
    ]
    data.each {message, result ->
      underTest.decide(event('foo.bar.baz', Level.INFO, message)) == result
    }
  }

  @Test
  void 'filter operation ends-with'() {
    def underTest = new MessageBlacklistFilter.Factory(
        logger: 'foo.bar.baz',
        level: Level.INFO,
        op: Operation.ENDS_WITH,
        value: 'test'
    ).build()

    def data = [
        'testing': FilterReply.NEUTRAL,
        'this is a test': FilterReply.DENY,
        'this is not a test': FilterReply.NEUTRAL
    ]
    data.each {message, result ->
      underTest.decide(event('foo.bar.baz', Level.INFO, message)) == result
    }
  }

  @Test
  void 'filter operation contains'() {
    def underTest = new MessageBlacklistFilter.Factory(
        logger: 'foo.bar.baz',
        level: Level.INFO,
        op: Operation.ENDS_WITH,
        value: 'test'
    ).build()

    def data = [
        'testing': FilterReply.DENY,
        'this is a test': FilterReply.DENY,
        'hello': FilterReply.NEUTRAL
    ]
    data.each {message, result ->
      underTest.decide(event('foo.bar.baz', Level.INFO, message)) == result
    }
  }
}
