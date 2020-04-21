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
package org.sonatype.goodies.dropwizard.aws.ecs

import org.junit.Before
import org.junit.Test

/**
 * {@link EcsTaskMetadataClient} tests.
 */
class EcsTaskMetadataClientTest
{
  EcsTaskMetadataClient underTest

  String metadataUriValue

  int discoveryCount

  @Before
  void setUp() {
    this.discoveryCount = 0
    this.underTest = new EcsTaskMetadataClient() {
      @Override
      String discoverMetadataUriValue() {
        discoveryCount++
        return metadataUriValue
      }
    }
  }

  @Test
  void 'when metadata-value missing unsupported'() {
    metadataUriValue = null
    assert !underTest.supported
    assert discoveryCount == 1

    // try again; should bypass discovery
    assert !underTest.supported
    assert discoveryCount == 1
  }

  @Test
  void 'when metadata-value present supported'() {
    metadataUriValue = 'http://localhost:1234/abcd'
    assert underTest.supported
    assert discoveryCount == 1

    // try again; should bypass discovery
    assert underTest.supported
    assert discoveryCount == 1
  }
}
