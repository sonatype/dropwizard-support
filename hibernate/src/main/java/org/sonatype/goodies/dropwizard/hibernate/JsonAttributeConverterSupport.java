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

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for JSON {@link AttributeConverter} implementations.
 *
 * @since 1.2.0
 */
@Beta
public class JsonAttributeConverterSupport<T>
    implements AttributeConverter<T, String>
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final JsonType<T> type;

  private final ObjectMapper objectMapper;

  public JsonAttributeConverterSupport(final JsonType<T> type, final ObjectMapper objectMapper) {
    this.type = checkNotNull(type);
    this.objectMapper = checkNotNull(objectMapper);
  }

  public JsonAttributeConverterSupport(final TypeReference<T> type, final ObjectMapper objectMapper) {
    this(JsonType.create(type), objectMapper);
  }

  public JsonAttributeConverterSupport(final Class<T> type, final ObjectMapper objectMapper) {
    this(JsonType.create(type), objectMapper);
  }

  @Override
  public String convertToDatabaseColumn(final T model) {
    log.trace("Converting to column: {}", model);
    try {
      String value = type.writer(objectMapper).writeValueAsString(model);
      log.trace("Converted: {}", value);
      return value;
    }
    catch (Exception e) {
      log.trace("Failed", e);
      throw new RuntimeException(String.format("Failed to convert to string column: %s", model), e);
    }
  }

  @Override
  public T convertToEntityAttribute(final String value) {
    log.trace("Converting to model: {}", value);
    try {
      T model = type.reader(objectMapper).readValue(value);
      log.trace("Converted: {}", model);
      return model;
    }
    catch (Exception e) {
      log.trace("Failed", e);
      throw new RuntimeException(String.format("Failed to convert to %s model: %s", type, value), e);
    }
  }
}
