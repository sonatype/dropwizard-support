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
package org.sonatype.goodies.dropwizard.text

import com.google.common.collect.ImmutableList
import org.junit.jupiter.api.Test

// copied from: https://github.com/sonatype/nexus-internal/blob/master/components/nexus-common/src/test/java/org/sonatype/nexus/common/text/PluralTest.groovy

/**
 * Tests for {@link Plural}
 */
class PluralTest
{
  @Test
  void 'simple plural'() {
    assert Plural.of(-1, 'dog') == '-1 dogs'
    assert Plural.of(0, 'dog') == '0 dogs'
    assert Plural.of(1, 'dog') == '1 dog'
    assert Plural.of(2, 'dog') == '2 dogs'
  }

  @Test
  void 'simple plural collection'() {
    assert Plural.of(Collections.emptyList(), 'dog') == '0 dogs'
    assert Plural.of(ImmutableList.of('fido'), 'dog') == '1 dog'
    assert Plural.of(ImmutableList.of('fido', 'spot'), 'dog') == '2 dogs'
  }

  @Test
  void 'complex plural'() {
    assert Plural.of(-1, 'candy', 'candies') == '-1 candies'
    assert Plural.of(0, 'candy', 'candies') == '0 candies'
    assert Plural.of(1, 'candy', 'candies') == '1 candy'
    assert Plural.of(2, 'candy', 'candies') == '2 candies'
  }
}