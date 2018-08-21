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
package org.sonatype.goodies.dropwizard.testsupport;

import java.nio.charset.StandardCharsets;

import com.google.common.io.BaseEncoding;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Basic authentication helper.
 *
 * @since ???
 */
public class BasicAuthentication
{
  private BasicAuthentication() {
    // empty
  }

  public static String headerValue(final String username, final String password) {
    checkNotNull(username);
    checkNotNull(password);
    String encoded = BaseEncoding.base64().encode(bytesOf(username + ":" + password));
    return "Basic " + encoded;
  }

  private static byte[] bytesOf(final String value) {
    return value.getBytes(StandardCharsets.ISO_8859_1);
  }
}
