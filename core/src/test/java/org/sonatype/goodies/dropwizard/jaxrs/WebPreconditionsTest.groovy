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
package org.sonatype.goodies.dropwizard.jaxrs

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response.Status

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.fail

/**
 * {@link WebPreconditions} tests.
 */
class WebPreconditionsTest
{
  @Test
  void 'check-found with object'() {
    try {
      WebPreconditions.checkFound(null)
      fail()
    }
    catch (WebApplicationException expected) {
      assert expected.response.status == Status.NOT_FOUND.statusCode
    }

    def value = 'foo'
    def result = WebPreconditions.checkFound(value)
    assert result == value
  }
}
