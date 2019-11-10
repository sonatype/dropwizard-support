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

import java.util.LinkedHashMap;
import java.util.Map;

import org.sonatype.goodies.dropwizard.jaxrs.ResourceSupport;
import org.sonatype.goodies.dropwizard.jaxrs.WebPreconditions;

/**
 * {@link EcsTaskMetadataEndpoint} resource.
 *
 * @since ???
 */
//@Named
//@Singleton
public class EcsTaskMetadataResource
    extends ResourceSupport
    implements EcsTaskMetadataEndpoint
{
  private final EcsTaskMetadataClient metadataClient;

  public EcsTaskMetadataResource() {
    this.metadataClient = new EcsTaskMetadataClient();
  }

  @Override
  public Map<String, Object> get() {
    WebPreconditions.checkFound(metadataClient.isSupported(), "ECS task-metadata not supported");
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("container", metadataClient.getContainerMetadata());
    result.put("container-stats", metadataClient.getContainerStats());
    result.put("task", metadataClient.getTaskMetadata());
    result.put("task-stats", metadataClient.getTaskStats());
    return result;
  }
}
