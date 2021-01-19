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
package org.sonatype.goodies.dropwizard.aws.s3

import org.sonatype.goodies.dropwizard.aws.s3.S3Location.InvalidLocationException

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertThrows

/**
 * {@link S3Location} tests.
 */
class S3LocationTest
{
  @Test
  void 'to URI'() {
    def underTest = new S3Location('foo', 'bar/baz')
    underTest.toUri() == URI.create('s3://foo/bar/baz')
  }

  @Test
  void 'parse invalid scheme'() {
    assertThrows(InvalidLocationException.class, {
      S3Location.parse(URI.create('http://example.com/foo'))
    })
  }

  @Test
  void 'parse missing path'() {
    S3Location.parse(URI.create('s3://a')).with {
      assert bucket == 'a'
      assert key == ''
    }
  }

  @Test
  void 'parse with path'() {
    S3Location.parse(URI.create('s3://a/b/c')).with {
      assert bucket == 'a'
      assert key == 'b/c'
    }
  }

  @Test
  void 'normalization'() {
    def underTest = new S3Location('foo', 'bar/baz qux')
    underTest.toString() == 's3://foo/bar/baz quz'
    underTest.toUri() == URI.create('s3://foo/bar/baz%20qux')
  }
}
