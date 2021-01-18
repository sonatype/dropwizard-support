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
package org.sonatype.goodies.dropwizard.jackson;

import java.io.IOException;
import java.util.EnumSet;

import javax.annotation.Nonnull;

/**
 * Deserialize {@link Enum} from {@code toString()} values.
 *
 * @since ???
 */
public abstract class EnumToStringDeserializer<T extends Enum<T>>
    extends SimpleJsonDeserializer<T>
{
  private static final long serialVersionUID = 1L;

  private final EnumSet<T> values;

  public EnumToStringDeserializer(final Class<T> type) {
    super(type);
    this.values = EnumSet.allOf(type);
  }

  @Override
  protected T parse(@Nonnull final String value) throws IOException {
    for (T e : values) {
      if (e.toString().equals(value)) {
        return e;
      }
    }
    // TODO: should report warning/error here?
    return null;
  }
}
