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
package org.sonatype.goodies.dropwizard.config

import org.sonatype.goodies.dropwizard.config.FirstMatchStringLookup.MissingSubstitutionValueException

import org.apache.commons.text.lookup.StringLookup
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertThrows

/**
 * {@link FirstMatchStringLookup} tests.
 */
class FirstMatchStringLookupTest
{
  @Test
  void 'missing substitution value throws exception when strict'() {
    StringLookup keyUnlessMissing = { key ->
      if (key == 'missing') {
        return null
      }
      return key
    }

    def underTest = new FirstMatchStringLookup(true, keyUnlessMissing)

    underTest.lookup('example') == 'example'
    assertThrows(MissingSubstitutionValueException.class, {
      underTest.lookup('missing')
    })
  }
}
