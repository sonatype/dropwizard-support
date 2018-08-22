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

import org.sonatype.goodies.testsupport.TestSupport

import com.google.common.io.Resources
import org.junit.Test

/**
 * {@link Version} tests.
 */
class VersionTest
  extends TestSupport
{
  @Test
  void 'properties from URL'() {
    def underTest = new Version(Resources.getResource(getClass(), Version.RESOURCE))
    assert underTest.version == 'foo'
    assert underTest.timestamp == 'bar'
    assert underTest.tag == 'baz'
    assert underTest.notes == 'qux'
  }

  @Test
  void 'properties from resource'() {
    def underTest = new Version(getClass())
    assert underTest.version == 'foo'
    assert underTest.timestamp == 'bar'
    assert underTest.tag == 'baz'
    assert underTest.notes == 'qux'
  }

  @Test
  void 'properties missing are unknown'() {
    def props = new Properties()
    def underTest = new Version() {
      @Override
      protected Properties load() {
        return props
      }
    }
    assert underTest.version == Version.UNKNOWN
    assert underTest.timestamp == Version.UNKNOWN
    assert underTest.tag == Version.UNKNOWN
    assert underTest.notes == Version.UNKNOWN
  }

  @Test
  void 'properties non-resolved are unknown'() {
    def props = new Properties()
    props.setProperty(Version.VERSION, '${foo}')
    props.setProperty(Version.TIMESTAMP, '${bar}')
    props.setProperty(Version.TAG, '${baz}')
    props.setProperty(Version.NOTES, '${qux}')

    def underTest = new Version() {
      @Override
      protected Properties load() {
        return props
      }
    }
    assert underTest.version == Version.UNKNOWN
    assert underTest.timestamp == Version.UNKNOWN
    assert underTest.tag == Version.UNKNOWN
    assert underTest.notes == Version.UNKNOWN
  }
}
