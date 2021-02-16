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

import java.lang.annotation.Annotation;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.jaxrs.Resource;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.google.inject.Key;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverters;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.eclipse.sisu.inject.BeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Swagger model.
 *
 * @see SwaggerContributor
 * @since 1.0.0
 */
@Singleton
public class SwaggerModel
    extends ServiceSupport
{
  private static final Logger log = LoggerFactory.getLogger(SwaggerModel.class);

  private final BeanLocator beanLocator;

  private final List<SwaggerContributor> contributors;

  private final Reader reader;

  @Inject
  public SwaggerModel(final BeanLocator beanLocator,
                      final List<SwaggerContributor> contributors,
                      final List<ModelConverter> converters)
  {
    this.beanLocator = checkNotNull(beanLocator);
    this.contributors = checkNotNull(contributors);

    // register converters
    for (ModelConverter converter : converters) {
      log.debug("Converter: {}", converter);
      ModelConverters.getInstance().addConverter(converter);
    }

    this.reader = new Reader(createSwagger());
  }

  private static class ResourceMediator
      implements Mediator<Named, Resource, SwaggerModel>
  {
    @Override
    public void add(final BeanEntry<Named, Resource> entry, final SwaggerModel model) {
      model.scan(entry.getImplementationClass());
    }

    @Override
    public void remove(final BeanEntry<Named, Resource> entry, final SwaggerModel model) {
      // empty
    }
  }

  @Override
  protected void doStart() throws Exception {
    // scan all bound resources
    for (BeanEntry<Annotation, Resource> entry : beanLocator.locate(Key.get(Resource.class))) {
      scan(entry.getImplementationClass());
    }

    // watch for more resources
    beanLocator.watch(Key.get(Resource.class, Named.class), new ResourceMediator(), this);

    // apply all contributors
    contributors.forEach(c -> c.contribute(getSwagger()));
  }

  public void scan(final Class<Resource> type) {
    checkNotNull(type);
    log.debug("Scan: {}", type);
    reader.read(type);
  }

  public Swagger getSwagger() {
    return reader.getSwagger();
  }

  protected Swagger createSwagger() {
    return new Swagger();
  }
}
