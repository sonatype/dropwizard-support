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
package org.sonatype.goodies.dropwizard.aws.ecs;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * ECS task-metadata endpoint.
 *
 * @since ???
 */
@Path("/api/internal/ecs-task-metadata")
@Api(value = "Internal")
public interface EcsTaskMetadataEndpoint
{
  @GET
  @Produces({APPLICATION_JSON})
  @ApiOperation(value = "Get ECS task-metadata")
  @ApiResponses({
      @ApiResponse(code = 200, message = "ECS task-metadata"),
      @ApiResponse(code = 404, message = "ECS task-metadata not supported")
  })
  Map<String,Object> get();
}
