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
package com.sonatype.ossindex.dropwizard.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Holder of random things.
 *
 * @since ???
 */
public final class Rando
{
  private Rando() {
    // empty
  }

  public static final Random random = new SecureRandom();

  /**
   * Generate a random integer in given range.
   */
  public static int range(final int min, final int max) {
    return random.nextInt((max - min) + 1) + min;
  }

  /**
   * Generate random bytes.
   */
  public static byte[] bytes(final int size) {
    checkArgument(size > 0);
    byte[] bytes = new byte[size];
    random.nextBytes(bytes);
    return bytes;
  }

  /**
   * Generate a random token with given number of bytes.
   */
  public static String token(final int bytes) {
    return new BigInteger(bytes(bytes)).abs().toString(32);
  }
}
