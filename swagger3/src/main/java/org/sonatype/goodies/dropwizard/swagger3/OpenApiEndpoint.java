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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * OpenAPI endpoint.
 *
 * @since ???
 */
@Path("/openapi.{type:json|yaml}")
@Tag(name = "Service")
public interface OpenApiEndpoint
{
  String APPLICATION_YAML = "application/yaml";

  enum Type
  {
    json(APPLICATION_JSON),
    yaml(APPLICATION_YAML);

    public final String contentType;

    Type(final String contentType) {
      this.contentType = contentType;
    }
  }

  @GET
  @Produces({APPLICATION_JSON, APPLICATION_YAML})
  @Operation(
      summary = "Get OpenAPI model",
      responses = {
          @ApiResponse(responseCode = "200", description = "OpenAPI model", content = {
              // omit schema details to avoid bloating response
              @Content(mediaType = APPLICATION_JSON/*, schema = @Schema(implementation = OpenAPI.class, hidden = true)*/),
              @Content(mediaType = APPLICATION_YAML/*, schema = @Schema(implementation = OpenAPI.class, hidden = true)*/)
          }),
          @ApiResponse(responseCode = "404", description = "Not found")
      }
  )
  Response get(
      @PathParam("type") @Parameter(description = "Model type") Type type,
      @QueryParam("pretty") @Parameter(description = "Pretty print") boolean pretty
  ) throws IOException;
}
