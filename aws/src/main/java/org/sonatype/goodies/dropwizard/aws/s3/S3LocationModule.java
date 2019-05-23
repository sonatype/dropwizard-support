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
package org.sonatype.goodies.dropwizard.aws.s3;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * {@link S3Location} Jackson module.
 *
 * @since ???
 */
public class S3LocationModule
    extends SimpleModule
{
  private static final long serialVersionUID = 1L;

  @Override
  public void setupModule(final SetupContext context) {
    addSerializer(S3Location.class, new S3LocationSerializer());
    addDeserializer(S3Location.class, new S3LocationDeserializer());

    super.setupModule(context);
  }

  /**
   * {@link S3Location} deserializer.
   */
  public static class S3LocationDeserializer
      extends StdDeserializer<S3Location>
  {
    private static final long serialVersionUID = 1L;

    public S3LocationDeserializer() {
      super(S3Location.class);
    }

    @Override
    public S3Location deserialize(final JsonParser parser, final DeserializationContext context)
        throws IOException, JsonProcessingException
    {
      String value = parser.readValueAs(String.class);
      return S3Location.parse(value);
    }
  }

  /**
   * {@link S3Location} serializer.
   */
  public static class S3LocationSerializer
      extends StdSerializer<S3Location>
  {
    private static final long serialVersionUID = 1L;

    public S3LocationSerializer() {
      super(S3Location.class);
    }

    @Override
    public void serialize(final S3Location value, final JsonGenerator generator, final SerializerProvider provider)
        throws IOException
    {
      generator.writeString(value.toString());
    }
  }
}
