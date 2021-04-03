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
package org.sonatype.goodies.dropwizard.swagger2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.sonatype.goodies.dropwizard.jaxrs.Resource;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Key;
import io.dropwizard.lifecycle.Managed;
import io.swagger.v3.core.filter.AbstractSpecFilter;
import io.swagger.v3.core.filter.OpenAPISpecFilter;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.inject.BeanLocator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
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

  private final Provider<HttpHeaders> headers;

  private final Provider<UriInfo> uriInfo;

  @Inject
  public OpenApiResource(
      final BeanLocator beanLocator,
      final Provider<HttpHeaders> headers,
      final Provider<UriInfo> uriInfo)
  {
    this.beanLocator = checkNotNull(beanLocator);
    this.headers = checkNotNull(headers);
    this.uriInfo = checkNotNull(uriInfo);
  }

  private HttpHeaders headers() {
    HttpHeaders result = headers.get();
    checkState(result != null, "Missing context: %s", HttpHeaders.class);
    return result;
  }

  private UriInfo uriInfo() {
    UriInfo result = uriInfo.get();
    checkState(result != null, "Missing context: %s", UriInfo.class);
    return result;
  }

  @Override
  protected void doStart() throws Exception {
    // scan for bound resources
    Set<String> resources = new LinkedHashSet<>();
    for (BeanEntry<Annotation, Resource> entry : beanLocator.locate(Key.get(Resource.class))) {
      resources.add(entry.getImplementationClass().getName());
    }

    SwaggerConfiguration config = new SwaggerConfiguration()
        .openAPI(new OpenAPI())
        .resourceClasses(resources);

    //noinspection rawtypes
    OpenApiContext context = new JaxrsOpenApiContextBuilder()
        .ctxId(OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT)
        .openApiConfiguration(config)
        .buildContext(true);

    // install custom reader to omit unresolved references on cached model
    context.setOpenApiReader(new FilterReader(new AbstractSpecFilter()
    {
      @Override
      public boolean isRemovingUnreferencedDefinitions() {
        return true;
      }
    }));

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

    OpenAPI model = context.read();
    checkFound(model);

    ObjectMapper mapper = mapper(context, type);
    ObjectWriter writer = pretty ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
    return Response.status(Status.OK)
        .type(type.contentType)
        .entity(writer.writeValueAsString(model))
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

  private class FilterReader
      extends Reader
  {
    private final OpenAPISpecFilter filter;

    public FilterReader(final OpenAPISpecFilter filter) {
      this.filter = checkNotNull(filter);
    }

    @Override
    public OpenAPI read(final Set<Class<?>> classes, final Map<String, Object> resources) {
      OpenAPI model = super.read(classes, resources);
      return SpecFilterHelper.filter(model, filter, uriInfo(), headers());
    }
  }
}
