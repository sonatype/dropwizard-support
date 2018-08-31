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
import javax.ws.rs.core.Response.StatusType;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link HealthCheck} helpers.
 *
 * @since ???
 */
public final class HealthCheckHelper
{
  private static final Logger log = LoggerFactory.getLogger(HealthCheckHelper.class);

  private HealthCheckHelper() {
    // empty
  }

  public static Result checkStatus(final WebTarget target, final Function<StatusType,Boolean> validator) {
    checkNotNull(target);
    checkNotNull(validator);

    try {
      log.trace("Checking status: {}", target);
      Response response = target.request().get();
      try {
        StatusType status = response.getStatusInfo();
        log.trace("Status: {}", status);

        Boolean healthy = validator.apply(status);
        if (healthy) {
          return HealthCheck.Result.healthy();
        }
        return HealthCheck.Result.unhealthy("status: " + status);
      }
      finally {
        response.close();
      }
    }
    catch (Exception e) {
      return HealthCheck.Result.unhealthy(e);
    }
  }
}
