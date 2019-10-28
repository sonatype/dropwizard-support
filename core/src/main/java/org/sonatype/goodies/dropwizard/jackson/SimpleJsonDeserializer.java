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

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

/**
 * Simple {@link JsonDeserializer}.
 *
 * @since ???
 */
public abstract class SimpleJsonDeserializer<T>
    extends StdScalarDeserializer<T>
{
  private static final long serialVersionUID = 1L;

  public SimpleJsonDeserializer(final Class<?> type) {
    super(type);
  }

  @Override
  public T deserialize(final JsonParser parser, final DeserializationContext context)
      throws IOException, JsonProcessingException
  {
    JsonToken token = parser.getCurrentToken();
    if (token == JsonToken.VALUE_STRING) {
      String value = parser.getText().trim();
      if (value.isEmpty()) {
        return (T)getEmptyValue(context);
      }
      return parse(value);
    }
    return (T)context.handleUnexpectedToken(this._valueClass, parser);
  }

  protected abstract T parse(@Nonnull String value) throws IOException;
}
