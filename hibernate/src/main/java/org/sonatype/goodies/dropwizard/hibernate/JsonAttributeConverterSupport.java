/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.hibernate;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for JSON {@link AttributeConverter} implementations.
 *
 * @since ???
 */
public abstract class JsonAttributeConverterSupport<T>
    implements AttributeConverter<T, String>
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final Class<T> type;

  private final ObjectMapper objectMapper;

  public JsonAttributeConverterSupport(final Class<T> type, final ObjectMapper objectMapper) {
    this.type = checkNotNull(type);
    this.objectMapper = checkNotNull(objectMapper);
  }

  @Override
  public String convertToDatabaseColumn(final T model) {
    log.trace("Converting to column: {}", model);
    try {
      String value = objectMapper.writeValueAsString(model);
      log.trace("Converted: {}", value);
      return value;
    }
    catch (Exception e) {
      throw new RuntimeException(String.format("Failed to convert to string column: %s", model), e);
    }
  }

  @Override
  public T convertToEntityAttribute(final String value) {
    log.trace("Converting to model: {}", value);
    try {
      T model = objectMapper.readValue(value, type);
      log.trace("Converted: {}", model);
      return model;
    }
    catch (Exception e) {
      throw new RuntimeException(String.format("Failed to convert to %s model: %s", type.getSimpleName(), value), e);
    }
  }
}
