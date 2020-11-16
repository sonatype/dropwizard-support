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
package org.sonatype.goodies.dropwizard.rules.matcher.string

import org.junit.jupiter.api.Test

import static org.sonatype.goodies.dropwizard.rules.matcher.string.StringMatcher2.Function.contains
import static org.sonatype.goodies.dropwizard.rules.matcher.string.StringMatcher2.Function.prefix
import static org.sonatype.goodies.dropwizard.rules.matcher.string.StringMatcher2.Function.suffix

/**
 * {@link StringMatcher2} tests.
 */
class StringMatcher2Test
{
  //
  // contains
  //

  @Test
  void 'contains case-sensitive'() {
    def underTest = new StringMatcher2(contains,'bar', false)
    println underTest
    assert underTest.matches('foo bar baz')
    assert !underTest.matches('FOO BAR BAZ')
    assert underTest.matches('a b c foo bar baz')
    assert !underTest.matches('A B C FOO BAR BAZ')
    assert !underTest.matches('a b c')
    assert !underTest.matches('A B C')
  }

  @Test
  void 'contains ignore-case'() {
    def underTest = new StringMatcher2(contains,'bar', true)
    println underTest
    assert underTest.matches('foo bar baz')
    assert underTest.matches('FOO BAR BAZ')
    assert underTest.matches('a b c foo bar baz')
    assert underTest.matches('A B C FOO BAR BAZ')
    assert !underTest.matches('a b c')
    assert !underTest.matches('A B C')
  }

  //
  // prefix
  //

  @Test
  void 'prefix case-sensitive'() {
    def underTest = new StringMatcher2(prefix,'foo', false)
    assert underTest.matches('foo bar baz')
    assert !underTest.matches('FOO BAR BAZ')
    assert !underTest.matches('a b c foo bar baz')
    assert !underTest.matches('A B C FOO BAR BAZ')
    assert !underTest.matches('a b c')
    assert !underTest.matches('A B C')
  }

  @Test
  void 'prefix ignore-case'() {
    def underTest = new StringMatcher2(prefix,'foo', true)
    assert underTest.matches('foo bar baz')
    assert underTest.matches('FOO BAR BAZ')
    assert !underTest.matches('a b c foo bar baz')
    assert !underTest.matches('A B C FOO BAR BAZ')
    assert !underTest.matches('a b c')
    assert !underTest.matches('A B C')
  }

  //
  // suffix
  //

  @Test
  void 'suffix case-sensitive'() {
    def underTest = new StringMatcher2(suffix, 'baz', false)
    assert underTest.matches('foo bar baz')
    assert !underTest.matches('FOO BAR BAZ')
    assert !underTest.matches('foo bar baz a b c')
    assert !underTest.matches('FOO BAR BAZ A B C')
    assert !underTest.matches('a b c')
    assert !underTest.matches('A B C')
  }

  @Test
  void 'suffix ignore-case'() {
    def underTest = new StringMatcher2(suffix, 'baz', true)
    assert underTest.matches('foo bar baz')
    assert underTest.matches('FOO BAR BAZ')
    assert !underTest.matches('foo bar baz a b c')
    assert !underTest.matches('FOO BAR BAZ A B C')
    assert !underTest.matches('a b c')
    assert !underTest.matches('A B C')
  }
}
