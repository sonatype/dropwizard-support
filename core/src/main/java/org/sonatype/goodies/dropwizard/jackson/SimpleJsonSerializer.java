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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * Simple {@link JsonSerializer}.
 *
 * @since ???
 */
public abstract class SimpleJsonSerializer<T>
    extends StdScalarSerializer<T>
{
  private static final long serialVersionUID = 1L;

  public SimpleJsonSerializer(final Class<T> type) {
    super(type);
  }

  @Override
  public void serializeWithType(final T value,
                                final JsonGenerator generator,
                                final SerializerProvider provider,
                                final TypeSerializer serializer) throws IOException
  {
    WritableTypeId typeId = serializer.writeTypePrefix(generator, serializer.typeId(value, JsonToken.VALUE_STRING));
    serialize(value, generator, provider);
    serializer.writeTypeSuffix(generator, typeId);
  }

  @Override
  public void serialize(final T value, final JsonGenerator generator, final SerializerProvider provider)
      throws IOException
  {
    generator.writeString(render(value));
  }

  protected abstract String render(@Nonnull T value) throws IOException;
}
