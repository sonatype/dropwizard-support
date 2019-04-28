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
package org.sonatype.goodies.dropwizard.util

import org.junit.Test

/**
 * {@link MoreStrings} tests.
 */
class MoreStringsTest
{
  @Test
  void 'blankToNull'() {
    assert MoreStrings.blankToNull(null) == null
    assert MoreStrings.blankToNull('') == null
    assert MoreStrings.blankToNull('      ') == null
    assert MoreStrings.blankToNull('foo') != null
    assert MoreStrings.blankToNull('  bar  ') != null

    // ensure that result is the same as given when its non-null or blank
    '  bar  '.with {
      assert MoreStrings.blankToNull(it) == it
    }
  }

  @Test
  void 'lower'() {
    def value = 'FooBar'
    assert MoreStrings.lower(value) == 'foobar'
  }

  @Test
  void 'upper'() {
    def value = 'FooBar'
    assert MoreStrings.upper(value) == 'FOOBAR'
  }
}
