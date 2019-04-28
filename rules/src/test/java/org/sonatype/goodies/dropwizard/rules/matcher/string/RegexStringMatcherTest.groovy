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

import org.junit.Test

/**
 * {@link RegexStringMatcher} tests.
 */
class RegexStringMatcherTest
{
  @Test
  void 'case-sensitive'() {
    def underTest = new RegexStringMatcher('.+bar.+')

    assert underTest.matches('foo bar baz')
    assert !underTest.matches('FOO BAR BAZ')
    assert underTest.matches('a b c foo bar baz')
    assert !underTest.matches('A B C FOO BAR BAZ')
    assert !underTest.matches('a b c')
    assert !underTest.matches('A B C')
  }

  @Test
  void 'case-sensitive inverted'() {
    def underTest = new RegexStringMatcher('.+bar.+')
    underTest.invert = true

    assert !underTest.matches('foo bar baz')
    assert underTest.matches('FOO BAR BAZ')
    assert !underTest.matches('a b c foo bar baz')
    assert underTest.matches('A B C FOO BAR BAZ')
    assert underTest.matches('a b c')
    assert underTest.matches('A B C')
  }

  @Test
  void 'ignore-case'() {
    def underTest = new RegexStringMatcher('.+bar.+')
    underTest.ignoreCase = true

    assert underTest.matches('foo bar baz')
    assert underTest.matches('FOO BAR BAZ')
    assert underTest.matches('a b c foo bar baz')
    assert underTest.matches('A B C FOO BAR BAZ')
    assert !underTest.matches('a b c')
    assert !underTest.matches('A B C')
  }

  @Test
  void 'ignore-case inverted'() {
    def underTest = new RegexStringMatcher('.+bar.+')
    underTest.ignoreCase = true
    underTest.invert = true

    assert !underTest.matches('foo bar baz')
    assert !underTest.matches('FOO BAR BAZ')
    assert !underTest.matches('a b c foo bar baz')
    assert !underTest.matches('A B C FOO BAR BAZ')
    assert underTest.matches('a b c')
    assert underTest.matches('A B C')
  }
}
