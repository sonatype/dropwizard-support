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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.annotation.Nonnull;

/**
 * Helper to wrap a {@link PrintWriter} with a {@link StringWriter} back-end buffer.
 *
 * @since ???
 */
public class PrintBuffer
    extends PrintWriter
{
  private final StringWriter buffer;

  public PrintBuffer(@Nonnull final StringWriter buffer) {
    super(buffer, false);
    this.buffer = (StringWriter)out;
  }

  public PrintBuffer() {
    this(new StringWriter());
  }

  public StringWriter getBuffer() {
    return buffer;
  }

  @Override
  public String toString() {
    flush();
    return buffer.toString();
  }
}
