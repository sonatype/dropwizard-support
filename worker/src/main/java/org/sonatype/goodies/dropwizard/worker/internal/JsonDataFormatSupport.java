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
package org.sonatype.goodies.dropwizard.worker.internal;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.spi.DataFormat;

/**
 * Support for JSON {@link DataFormat} instances.
 *
 * @since ???
 */
public class JsonDataFormatSupport
    extends JacksonDataFormat
{
  public JsonDataFormatSupport(final Class<?> type, final boolean pretty) {
    super(type);
    setObjectMapper(createObjectMapper());
    setPrettyPrint(pretty);
  }

  public JsonDataFormatSupport(final Class<?> type) {
    this(type, false);
  }

  protected ObjectMapper createObjectMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .setSerializationInclusion(Include.NON_NULL)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
