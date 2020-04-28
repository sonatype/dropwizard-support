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

import java.util.function.Function;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link HealthCheck} helpers.
 *
 * @since 1.2.0
 */
public final class HealthCheckHelper
{
  private static final Logger log = LoggerFactory.getLogger(HealthCheckHelper.class);

  private HealthCheckHelper() {
    // empty
  }

  /**
   * Helper to return a {@link Result} based on the status of an HTTP request.
   *
   * @param target    HTTP target to make a GET request to.
   * @param validator Function to evaluate if status indicates healthy (returns {@code true}) or
   *                  unhealthy (returns {@code false}).
   */
  public static Result checkStatus(final WebTarget target, final Function<StatusType, Boolean> validator) {
    checkNotNull(target);
    checkNotNull(validator);

    try {
      log.trace("Checking status: {}", target);
      Response response = target.request().get();
      try {
        StatusType status = response.getStatusInfo();
        log.trace("Status: {}", status);

        Boolean healthy = validator.apply(status);
        if (healthy != null && healthy) {
          return Result.healthy();
        }
        return Result.unhealthy("status: " + status);
      }
      finally {
        response.close();
      }
    }
    catch (Exception e) {
      return Result.unhealthy(e);
    }
  }

  /**
   * Helper to return {@link Result} based on the status of an HTTP request.
   *
   * @see #checkStatus(WebTarget, Function)
   */
  public static Result checkStatus(final WebTarget target, final StatusType status) {
    checkNotNull(status);
    return checkStatus(target, input -> input != null && input.getStatusCode() == status.getStatusCode());
  }

  /**
   * Helper to return {@link Result} based on the status family of an HTTP request.
   *
   * @see #checkStatus(WebTarget, Function)
   * @since 1.3.0
   */
  public static Result checkStatus(final WebTarget target, final Family family) {
    checkNotNull(family);
    return checkStatus(target, input -> input != null && input.getFamily() == family);
  }
}
