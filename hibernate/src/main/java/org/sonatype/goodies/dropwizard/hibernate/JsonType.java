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
package org.sonatype.goodies.dropwizard.hibernate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.annotations.Beta;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to deal with JSON types from {@link Class} or {@link TypeReference}.
 *
 * @since ???
 */
@Beta
public abstract class JsonType<T>
{
  public abstract String getTypeName();

  public abstract ObjectReader reader(ObjectMapper objectMapper);

  public abstract ObjectWriter writer(ObjectMapper objectMapper);

  @Override
  public String toString() {
    return getTypeName();
  }

  //
  // Factories
  //

  /**
   * Create {@link JsonType} for {@link Class}.
   */
  public static <V> JsonType<V> create(final Class<V> type) {
    checkNotNull(type);

    return new JsonType<V>()
    {
      @Override
      public String getTypeName() {
        return type.getName();
      }

      @Override
      public ObjectReader reader(final ObjectMapper objectMapper) {
        return objectMapper.readerFor(type);
      }

      @Override
      public ObjectWriter writer(final ObjectMapper objectMapper) {
        return objectMapper.writerFor(type);
      }
    };
  }

  /**
   * Create {@link JsonType} for {@link TypeReference}.
   */
  public static <V> JsonType<V> create(final TypeReference<V> type) {
    checkNotNull(type);

    return new JsonType<V>()
    {
      @Override
      public String getTypeName() {
        return type.getType().getTypeName();
      }

      @Override
      public ObjectReader reader(final ObjectMapper objectMapper) {
        return objectMapper.readerFor(type);
      }

      @Override
      public ObjectWriter writer(final ObjectMapper objectMapper) {
        return objectMapper.writerFor(type);
      }
    };
  }
}
