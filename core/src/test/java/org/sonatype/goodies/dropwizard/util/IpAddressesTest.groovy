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

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for {@link IpAddresses}.
 */
class IpAddressesTest
{
  private IpAddresses underTest

  @BeforeEach
  void setUp() {
    underTest = new IpAddresses()
  }

  @Test
  void 'add match remove'() {
    assert underTest.empty

    assert underTest.addAddress('1.2.3.4')
    assert underTest.size == 1
    assert !underTest.empty

    assert underTest.match('1.2.3.4')
    assert !underTest.match('5.6.7.8')

    assert underTest.removeAddress('1.2.3.4')
    assert underTest.size == 0
    assert underTest.empty

    assert !underTest.match('1.2.3.4')
  }

  @Test
  void 'add and remove trims'() {
    assert underTest.addAddress(' 1.2.3.4 ')
    assert underTest.match('1.2.3.4')
    assert !underTest.match('5.6.7.8')

    assert underTest.removeAddress(' 1.2.3.4 ')
    assert !underTest.match('1.2.3.4')
  }

  @Test
  void 'set addresses trims'() {
    underTest.setAddresses([' 1.2.3.4 '])
    assert underTest.match('1.2.3.4')
  }

  @Test
  void 'subnet match'() {
    assert underTest.addAddress('1.2.3.0/24')
    assert underTest.match('1.2.3.4')
    assert !underTest.match('5.6.7.8')
  }
}
