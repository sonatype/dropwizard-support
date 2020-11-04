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
package org.sonatype.goodies.dropwizard.version;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import org.sonatype.goodies.dropwizard.app.ApplicationMetadata;
import org.sonatype.goodies.dropwizard.jaxrs.ResourceSupport;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Support for {@link Version} resources.
 *
 * @since 1.2.0
 */
@Api(value = "Version")
public abstract class VersionResourceSupport
    extends ResourceSupport
{
  private final ApplicationMetadata applicationMetadata;

  @Inject
  public VersionResourceSupport(final ApplicationMetadata applicationMetadata) {
    this.applicationMetadata = checkNotNull(applicationMetadata);
  }

  @GET
  @Produces({APPLICATION_JSON})
  @ApiOperation(value = "Get version information")
  @ApiResponses({
      @ApiResponse(code = 200, message = "Version information")
  })
  public Version get() {
    return new Version(
        applicationMetadata.getVersion(),
        applicationMetadata.getBuildTimestamp(),
        applicationMetadata.getBuildTag(),
        applicationMetadata.getBuildNotes()
    );
  }
}
