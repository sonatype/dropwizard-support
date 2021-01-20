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
package org.sonatype.goodies.dropwizard.common.ip;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

//
// extracted/adjusted select bits from from org.eclipse.jetty.servlets.DoSFilter
//

//
//  ========================================================================
//  Copyright (c) 1995-2017 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

/**
 * IP matcher.
 *
 * @since 1.2.0
 */
public final class IpMatcher
{
  private static final Logger log = LoggerFactory.getLogger(IpMatcher.class);

  private static final String IPv4_GROUP = "(\\d{1,3})";

  private static final Pattern IPv4_PATTERN = Pattern.compile(
      IPv4_GROUP + "\\." + IPv4_GROUP + "\\." + IPv4_GROUP + "\\." + IPv4_GROUP);

  private static final String IPv6_GROUP = "(\\p{XDigit}{1,4})";

  private static final Pattern IPv6_PATTERN = Pattern.compile(
      IPv6_GROUP + ":" + IPv6_GROUP + ":" + IPv6_GROUP + ":" + IPv6_GROUP + ":" + IPv6_GROUP + ":" + IPv6_GROUP + ":" + IPv6_GROUP + ":" + IPv6_GROUP);

  private static final Pattern CIDR_PATTERN = Pattern.compile("([^/]+)/(\\d+)");

  private IpMatcher() {
    // empty
  }

  /**
   * Check if given candidate matched any of given addresses.
   */
  public static boolean match(final String candidate, final List<String> addresses) {
    checkNotNull(candidate);
    checkNotNull(addresses);

    for (String address : addresses) {
      if (address.contains("/")) {
        if (subnetMatch(address, candidate)) {
          return true;
        }
      }
      else {
        if (address.equals(candidate)) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean subnetMatch(String subnetAddress, String address) {
    Matcher cidrMatcher = CIDR_PATTERN.matcher(subnetAddress);
    if (!cidrMatcher.matches()) {
      return false;
    }

    String subnet = cidrMatcher.group(1);
    int prefix;
    try {
      prefix = Integer.parseInt(cidrMatcher.group(2));
    }
    catch (NumberFormatException x) {
      log.debug("Ignoring malformed CIDR address {}", subnetAddress);
      return false;
    }

    byte[] subnetBytes = addressToBytes(subnet);
    if (subnetBytes == null) {
      log.debug("Ignoring malformed CIDR address {}", subnetAddress);
      return false;
    }
    byte[] addressBytes = addressToBytes(address);
    if (addressBytes == null) {
      log.debug("Ignoring malformed remote address {}", address);
      return false;
    }

    // Comparing IPv4 with IPv6 ?
    int length = subnetBytes.length;
    if (length != addressBytes.length) {
      return false;
    }

    byte[] mask = prefixToBytes(prefix, length);

    for (int i = 0; i < length; ++i) {
      if ((subnetBytes[i] & mask[i]) != (addressBytes[i] & mask[i])) {
        return false;
      }
    }

    return true;
  }

  private static byte[] addressToBytes(String address) {
    Matcher ipv4Matcher = IPv4_PATTERN.matcher(address);
    if (ipv4Matcher.matches()) {
      byte[] result = new byte[4];
      for (int i = 0; i < result.length; ++i) {
        result[i] = Integer.valueOf(ipv4Matcher.group(i + 1)).byteValue();
      }
      return result;
    }
    else {
      Matcher ipv6Matcher = IPv6_PATTERN.matcher(address);
      if (ipv6Matcher.matches()) {
        byte[] result = new byte[16];
        for (int i = 0; i < result.length; i += 2) {
          int word = Integer.valueOf(ipv6Matcher.group(i / 2 + 1), 16);
          result[i] = (byte) ((word & 0xFF00) >>> 8);
          result[i + 1] = (byte) (word & 0xFF);
        }
        return result;
      }
    }
    return null;
  }

  private static byte[] prefixToBytes(int prefix, int length) {
    byte[] result = new byte[length];
    int index = 0;
    while (prefix / 8 > 0) {
      result[index] = -1;
      prefix -= 8;
      ++index;
    }

    if (index == result.length) {
      return result;
    }

    // Sets the _prefix_ most significant bits to 1
    result[index] = (byte) ~((1 << (8 - prefix)) - 1);
    return result;
  }
}
