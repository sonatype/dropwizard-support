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
package org.sonatype.goodies.dropwizard.swagger3;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.sonatype.goodies.dropwizard.jaxrs.Resource;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Key;
import io.dropwizard.lifecycle.Managed;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.inject.BeanLocator;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.goodies.dropwizard.jaxrs.WebPreconditions.checkFound;

/**
 * {@link OpenApiEndpoint} resource.
 *
 * @since ???
 */
@Singleton
@Path("/openapi.{type:json|yaml}")
@Tag(name = "Service")
public class OpenApiResource
    extends ServiceSupport
    implements OpenApiEndpoint, Managed, Resource
{
  private final BeanLocator beanLocator;

  @Inject
  public OpenApiResource(final BeanLocator beanLocator) {
    this.beanLocator = checkNotNull(beanLocator);
  }

  @Override
  protected void doStart() throws Exception {
    Set<String> resources = new LinkedHashSet<>();
    for (BeanEntry<Annotation, Resource> entry : beanLocator.locate(Key.get(Resource.class))) {
      resources.add(entry.getImplementationClass().getName());
    }

    OpenAPI api = new OpenAPI();
    SwaggerConfiguration config = new SwaggerConfiguration()
        .openAPI(api)
        .resourceClasses(resources);

    //noinspection rawtypes
    OpenApiContext context = new JaxrsOpenApiContextBuilder()
        .ctxId(OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT)
        .openApiConfiguration(config)
        .buildContext(true);

    log.debug("Context: {} -> {}", context.getId(), context);
  }

  @Override
  public Response get(final Type type, final boolean pretty) throws IOException {
    OpenApiContext context;
    try {
      //noinspection rawtypes
      context = new JaxrsOpenApiContextBuilder()
          .ctxId(OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT)
          .buildContext(true);
    }
    catch (OpenApiConfigurationException e) {
      throw new RuntimeException(e);
    }

    OpenAPI api = context.read();
    checkFound(api);

    ObjectMapper mapper = mapper(context, type);
    ObjectWriter writer = pretty ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
    return Response.status(Status.OK)
        .type(type.contentType)
        .entity(writer.writeValueAsString(api))
        .build();
  }

  private static ObjectMapper mapper(final OpenApiContext context, final Type type) {
    switch (type) {
      case json:
        return context.getOutputJsonMapper();
      case yaml:
        return context.getOutputYamlMapper();
    }
    throw new RuntimeException("Invalid type: " + type);
  }
}
