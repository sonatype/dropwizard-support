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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.coursera.metrics.datadog.DynamicTagsCallback;
import org.coursera.metrics.datadog.DynamicTagsCallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apply dynamic tags from ECS task-metadata.
 *
 * Provides additional tags from ECS taskMetadata-metadata-endpoint which can not otherwise be defaulted in service
 * definitions.
 *
 * @see EcsTaskMetadataClient
 * @since ???
 */
public class EcsDynamicTagsCallback
    implements DynamicTagsCallback
{
  private static final Logger log = LoggerFactory.getLogger(EcsDynamicTagsCallback.class);

  private final EcsTaskMetadataClient metadataClient;

  @Nullable
  private Map<String, Object> containerMetadata;

  @Nullable
  private Map<String, Object> taskMetadata;

  public EcsDynamicTagsCallback() {
    this.metadataClient = new EcsTaskMetadataClient();
  }

  @Override
  public List<String> getTags() {
    // short-circuit if not supported; complain as this is probably a misconfiguration
    if (!metadataClient.isSupported()) {
      log.warn("ECS task-metadata is not supported; unable to add additional tags");
      return Collections.emptyList();
    }

    // extract and cache container and task metadata; the bits for tag generation will not change between requests
    if (containerMetadata == null) {
      containerMetadata = metadataClient.getContainerMetadata();
    }
    if (taskMetadata == null) {
      taskMetadata = metadataClient.getTaskMetadata();
    }

    Object containerId = containerMetadata.get("DockerId");
    Object containerName = containerMetadata.get("DockerName");
    String imageName = String.valueOf(containerMetadata.get("Image"));
    String imageShortName = imageName.substring(imageName.lastIndexOf("/") + 1, imageName.lastIndexOf(':'));
    String imageTag = imageName.substring(imageName.lastIndexOf(':') + 1);
    Object taskArn = taskMetadata.get("TaskARN");
    Object taskVersion = taskMetadata.get("Revision");

    // these are standard tags which the datadog/agent will apply to ecs.fargate.* metrics, this aligns those to the dogstatsd metrics
    Map<String, Object> tags = new LinkedHashMap<>();
    tags.put("container_id", containerId);
    tags.put("container_name", containerName);
    tags.put("docker_name", containerName);
    tags.put("docker_image", imageName);
    tags.put("image_name", imageName);
    tags.put("image_tag", imageTag);
    tags.put("short_image", imageShortName);
    tags.put("task_arn", taskArn);
    tags.put("task_version", taskVersion);
    tags.put("ecs_task_version", taskVersion);

    // these are the other tags which are presently configured via dogstatd
    // cluster_name, task_family, ecs_cluster, ecs_container_name, ecs_task_family, region

    log.trace("Tags: {}", tags);

    return tags.entrySet().stream()
        .map(e -> String.format("%s:%s", e.getKey(), e.getValue()))
        .collect(Collectors.toList());
  }

  //
  // Factory
  //

  @JsonTypeName("ecs")
  public static class Factory
      implements DynamicTagsCallbackFactory
  {
    @Override
    public DynamicTagsCallback build() {
      return new EcsDynamicTagsCallback();
    }
  }
}
