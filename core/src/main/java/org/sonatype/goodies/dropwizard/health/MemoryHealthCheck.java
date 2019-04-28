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
package org.sonatype.goodies.dropwizard.health;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.dropwizard.util.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Check health of memory.
 *
 * @since ???
 */
@Named("memory")
@Singleton
public class MemoryHealthCheck
    extends HealthCheck
{
  private static final Logger log = LoggerFactory.getLogger(MemoryHealthCheck.class);

  public static class Configuration
  {
    @Nullable
    @JsonProperty
    private Size freeBytes;

    @Nullable
    @JsonProperty
    private Double freePercent;

    @Nullable
    public Size getFreeBytes() {
      return freeBytes;
    }

    public void setFreeBytes(@Nullable final Size freeBytes) {
      this.freeBytes = freeBytes;
    }

    @Nullable
    public Double getFreePercent() {
      return freePercent;
    }

    public void setFreePercent(@Nullable final Double freePercent) {
      this.freePercent = freePercent;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("freeBytes", freeBytes)
          .add("freePercent", freePercent)
          .toString();
    }
  }

  private final Configuration config;

  @Inject
  public MemoryHealthCheck(final Configuration config) {
    this.config = checkNotNull(config);
    log.info("Config: {}", config);
  }

  @Override
  protected Result check() throws Exception {
    Runtime runtime = Runtime.getRuntime();

    long totalBytes = runtime.totalMemory();
    long freeBytes = runtime.freeMemory();
    double freePercent = ((double)freeBytes / (double)totalBytes) * 100;

    log.debug("total={} bytes, free={} bytes, %free={}",
        totalBytes,
        freeBytes,
        freePercent
    );

    ResultBuilder builder = Result.builder()
        .withDetail("total-bytes", totalBytes)
        .withDetail("free-bytes", freeBytes)
        .withDetail("free-percent", freePercent);

    if (config.freeBytes != null && freeBytes < config.freeBytes.toBytes()) {
      builder.unhealthy().withMessage("Minimum free-bytes exceeds threshold: %s", config.freeBytes);
    }
    else if (config.freePercent != null && freePercent < config.freePercent) {
      builder.unhealthy().withMessage("Minimum free-percent exceeds threshold: %s", config.freePercent);
    }
    else {
      builder.healthy();
    }

    return builder.build();
  }
}
