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
package org.sonatype.goodies.dropwizard.swagger;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.jaxrs.Resource;

import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverters;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Swagger model.
 *
 * @see SwaggerConfiguration
 * @see SwaggerContributor
 * @since ???
 */
@Named
@Singleton
public class SwaggerModel
{
  private static final Logger log = LoggerFactory.getLogger(SwaggerModel.class);

  private final SwaggerConfiguration config;

  private final List<SwaggerContributor> contributors;

  private final Reader reader;

  @Inject
  public SwaggerModel(final SwaggerConfiguration config,
                      final List<SwaggerContributor> contributors,
                      final List<ModelConverter> converters)
  {
    this.config = checkNotNull(config);
    this.contributors = checkNotNull(contributors);

    // register converters
    for (ModelConverter converter : converters) {
      log.debug("Converter: {}", converter);
      ModelConverters.getInstance().addConverter(converter);
    }

    this.reader = new Reader(createSwagger());
  }

  public void scan(final Class<Resource> resourceClass) {
    reader.read(resourceClass);
    contributors.forEach(c -> c.contribute(getSwagger()));
  }

  public Swagger getSwagger() {
    return reader.getSwagger();
  }

  private Swagger createSwagger() {
    return new Swagger().info(new Info()
        .title(config.getTitle())
        .version(config.getVersion())
        .description(config.getDescription())
    );
  }
}
