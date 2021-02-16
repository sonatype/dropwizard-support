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

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.sonatype.goodies.dropwizard.jaxrs.Resource;

import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.models.Swagger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Swagger resource.
 *
 * @see SwaggerModel
 * @since 1.0.0
 */
@Singleton
@Path("/swagger.{type:json|yaml}")
public class SwaggerResource
    extends ApiListingResource
    implements Resource
{
  private final SwaggerModel model;

  @Inject
  public SwaggerResource(final SwaggerModel model) { // NOSONAR
    this.model = checkNotNull(model);
  }

  @Override
  protected Swagger process(final Application app,
                            final ServletContext servletContext,
                            final ServletConfig servletConfig,
                            final HttpHeaders httpHeaders,
                            final UriInfo uriInfo)
  {
    // update cached model to use base path calculated from incoming request
    return model.getSwagger().basePath(uriInfo.getBaseUri().getPath());
  }
}
