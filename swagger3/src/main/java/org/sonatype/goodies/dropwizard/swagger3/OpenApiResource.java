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

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.sonatype.goodies.dropwizard.jaxrs.Resource;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Key;
import io.dropwizard.lifecycle.Managed;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.inject.BeanLocator;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.sonatype.goodies.dropwizard.jaxrs.WebPreconditions.checkFound;

/**
 * OpenAPI resource.
 *
 * @since ???
 */
@Singleton
@Path("/openapi.{type:json|yaml}")
public class OpenApiResource
    extends ServiceSupport
    implements Managed, Resource
{
  public static final String APPLICATION_YAML = "application/yaml";

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

    log.debug("Context: {}", context);
  }

  enum Type
  {
    JSON(APPLICATION_JSON),
    YAML(APPLICATION_YAML);

    final String contentType;

    Type(final String contentType) {
      this.contentType = contentType;
    }

    ObjectMapper mapper(final OpenApiContext ctx) {
      switch (this) {
        case JSON:
          return ctx.getOutputJsonMapper();
        case YAML:
          return ctx.getOutputYamlMapper();
      }
      throw new RuntimeException();
    }
  }

  @GET
  @Produces({APPLICATION_JSON, APPLICATION_YAML})
  @Operation(hidden = true)
  public Response getOpenApi(final @Context HttpHeaders headers,
                             final @Context UriInfo uriInfo,
                             final @PathParam("type") Type type,
                             final @QueryParam("pretty") boolean pretty)
      throws Exception
  {
    //noinspection rawtypes
    OpenApiContext context = new JaxrsOpenApiContextBuilder()
        .ctxId(OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT)
        .buildContext(true);

    OpenAPI api = context.read();
    checkFound(api);

    ObjectMapper mapper = type.mapper(context);
    ObjectWriter writer = pretty ? mapper.writer(new DefaultPrettyPrinter()) : mapper.writer();
    return Response.status(Status.OK)
        .type(type.contentType)
        .entity(writer.writeValueAsString(api))
        .build();
  }
}
