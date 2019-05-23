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
package org.sonatype.goodies.dropwizard.config;

import java.io.IOException;

import javax.validation.Validator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Configuration;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.LogbackModule;

/**
 * {@link ConfigurationFactoryFactory} support.
 *
 * @since ???
 */
public class ConfigurationFactoryFactorySupport<T extends Configuration>
    implements ConfigurationFactoryFactory<T>
{
  @Override
  public ConfigurationFactory<T> create(final Class<T> type,
                                        final Validator validator,
                                        final ObjectMapper objectMapper,
                                        final String propertyPrefix)
  {
    return new YamlConfigurationFactory<T>(type, validator, configure(objectMapper.copy()), propertyPrefix)
    {
      @Override
      protected T build(final JsonNode node, final String path)
          throws IOException, ConfigurationException
      {
        T config = super.build(node, path);
        customize(config);
        return config;
      }
    };
  }

  /**
   * Adjust {@link ObjectMapper} to include some more specifics and modules.
   */
  protected ObjectMapper configure(final ObjectMapper objectMapper) {
    objectMapper.registerModule(new LogbackModule());
    objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    return objectMapper;
  }

  /**
   * Allow for customization.
   */
  protected void customize(final T config) {
    // empty
  }
}
