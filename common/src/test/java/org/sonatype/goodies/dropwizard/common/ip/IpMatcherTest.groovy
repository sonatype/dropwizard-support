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
package org.sonatype.goodies.dropwizard.common.ip

import org.junit.jupiter.api.Test

/**
 * Tests for {@link IpMatcher}.
 */
class IpMatcherTest
{
  @Test
  void 'match ipv4'() {
    assert IpMatcher.match('1.2.3.4', [ '1.2.3.4', '5.6.7.8' ])
    assert !IpMatcher.match('6.6.6.0', [ '1.2.3.4', '5.6.7.8' ])
  }

  @Test
  void 'match ipv4 subnet'() {
    assert IpMatcher.match('1.2.3.4', [ '1.2.3.0/24', '5.6.7.0/24' ])
    assert !IpMatcher.match('6.6.6.0', [ '1.2.3.0/24', '5.6.7.0/24' ])
  }
}
