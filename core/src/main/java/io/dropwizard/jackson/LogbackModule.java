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
package io.dropwizard.jackson;

import java.io.IOException;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;

// Support for level serialization was removed; copied from: https://raw.githubusercontent.com/dropwizard/dropwizard/a68f1db8aa6ff0d738879a8f6e07d90bb43bdadf/dropwizard-jackson/src/main/java/io/dropwizard/jackson/LogbackModule.java

public class LogbackModule
    extends Module
{
  private static class LevelDeserializer
      extends JsonDeserializer<Level>
  {
    @Override
    public Level deserialize(JsonParser jp,
                             DeserializationContext ctxt) throws IOException
    {

      final String text = jp.getText();

      // required because YAML maps "off" to a boolean false
      if ("false".equalsIgnoreCase(text)) {
        return Level.OFF;
      }

      // required because YAML maps "on" to a boolean true
      if ("true".equalsIgnoreCase(text)) {
        return Level.ALL;
      }

      return Level.toLevel(text, Level.INFO);
    }
  }

  private static class LogbackDeserializers
      extends Deserializers.Base
  {
    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type,
                                                    DeserializationConfig config,
                                                    BeanDescription beanDesc) throws JsonMappingException
    {
      if (Level.class.isAssignableFrom(type.getRawClass())) {
        return new LevelDeserializer();
      }
      return super.findBeanDeserializer(type, config, beanDesc);
    }
  }

  private static class LevelSerializer
      extends JsonSerializer<Level>
  {
    @Override
    public void serialize(Level value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      jgen.writeString(value.toString());
    }
  }

  private static class LogbackSerializers
      extends Serializers.Base
  {
    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
      if (Level.class.isAssignableFrom(type.getRawClass())) {
        return new LevelSerializer();
      }
      return super.findSerializer(config, type, beanDesc);
    }
  }

  @Override
  public String getModuleName() {
    return "LogbackModule";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }

  @Override
  public void setupModule(SetupContext context) {
    context.addSerializers(new LogbackSerializers());
    context.addDeserializers(new LogbackDeserializers());
  }
}
