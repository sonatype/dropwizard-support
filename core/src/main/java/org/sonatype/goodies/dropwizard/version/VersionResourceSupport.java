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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

// TODO: remove, the value of this is pretty low and the complexity to maintain swagger annotations here non-zero

/**
 * Support for {@link Version} resources.
 *
 * @since 1.2.0
 */
public abstract class VersionResourceSupport
    extends ResourceSupport
{
  private ApplicationMetadata applicationMetadata;

  @Inject
  public void configure(final ApplicationMetadata applicationMetadata) {
    this.applicationMetadata = checkNotNull(applicationMetadata);
  }

  @GET
  @Produces({APPLICATION_JSON})
  @Operation(summary = "Get version information",
    responses = {
        @ApiResponse(responseCode = "200", description = "Version information", content = {@Content(schema = @Schema(implementation = Version.class))})
    }
  )
  public Version get() {
    checkState(applicationMetadata != null, "Not configured");
    return new Version(
        applicationMetadata.getVersion(),
        applicationMetadata.getBuildTimestamp(),
        applicationMetadata.getBuildTag(),
        applicationMetadata.getBuildNotes()
    );
  }
}
