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
package org.sonatype.goodies.dropwizard.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * URL helpers.
 *
 * @since 1.0.0
 */
public final class Urls
{
  private Urls() {
    // empty
  }

  /**
   * Create a URL from value.
   */
  public static URL create(final String value) {
    try {
      return new URL(value);
    }
    catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Create a URL from URI.
   *
   * @since 1.2.0
   */
  public static URL create(final URI uri) {
    try {
      return uri.toURL();
    }
    catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private static final String UTF_8 = "UTF-8";

  /**
   * URL-encode given value.
   */
  public static String encode(final String value) {
    checkNotNull(value);

    try {
      return URLEncoder.encode(value, UTF_8);
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * URL-decode given value.
   */
  public static String decode(final String value) {
    checkNotNull(value);

    try {
      return URLDecoder.decode(value, UTF_8);
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
