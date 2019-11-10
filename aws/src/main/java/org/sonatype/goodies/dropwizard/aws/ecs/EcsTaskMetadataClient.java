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

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetches ECS task-metadata client.
 *
 * @see <a href="https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-metadata-endpoint-v3.html">task-metadata-endpoint-v3</a>
 * @since ???
 */
public class EcsTaskMetadataClient
{
  private static final Logger log = LoggerFactory.getLogger(EcsTaskMetadataClient.class);

  public static final String METADATA_URI_VAR = "ECS_CONTAINER_METADATA_URI";

  private static final TypeReference<Map<String, Object>> MAP_STRING_OBJECT = new TypeReference<Map<String, Object>>() { };

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Nullable
  private URI metadataUri;

  @Nullable
  private Boolean supported;

  @Nullable
  public URI getMetadataUri() {
    if (metadataUri == null && (supported == null || supported)) {
      String value = discoverMetadataUriValue();
      if (value == null) {
        log.warn("Unsupported; missing env-var: {}", METADATA_URI_VAR);
        supported = false;
        return null;
      }
      metadataUri = URI.create(value);
      log.info("Metadata URI: {}", metadataUri);
    }
    return metadataUri;
  }

  @Nullable
  @VisibleForTesting
  String discoverMetadataUriValue() {
    return System.getenv(METADATA_URI_VAR);
  }

  public boolean isSupported() {
    return getMetadataUri() != null;
  }

  @Nonnull
  private Map<String, Object> fetch(final @Nullable String path) {
    URI uri = getMetadataUri();
    if (uri != null) {
      if (path != null) {
        uri = URI.create(String.format("%s/%s", uri, path));
      }
      try {
        log.debug("Fetching metadata: {}", uri);
        Map<String, Object> result = objectMapper.readValue(uri.toURL(), MAP_STRING_OBJECT);
        log.debug("Metadata: {}", result);
        return result;
      }
      catch (IOException e) {
        log.warn("Failed to read metadata from: {}", uri, e);
      }
    }
    return Collections.emptyMap();
  }

  /**
   * Returns metadata for container; or empty-map if unsupported.
   */
  @Nonnull
  public Map<String, Object> getContainerMetadata() {
    return fetch(null);
  }

  /**
   * Returns stats for container; or empty-map if unsupported.
   */
  @Nonnull
  public Map<String, Object> getContainerStats() {
    return fetch("stats");
  }

  /**
   * Returns metadata for task; or empty-map if unsupported.
   */
  @Nonnull
  public Map<String, Object> getTaskMetadata() {
    return fetch("task");
  }

  /**
   * Returns stats for task; or empty-map if unsupported.
   */
  @Nonnull
  public Map<String, Object> getTaskStats() {
    return fetch("task/stats");
  }
}
