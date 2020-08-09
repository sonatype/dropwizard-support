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
package org.sonatype.goodies.dropwizard.aws.s3;

import org.sonatype.goodies.dropwizard.health.HealthCheckSupport;

import com.codahale.metrics.health.HealthCheck;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * AWS S3 bucket {@link HealthCheck}.
 *
 * @since ???
 */
public class S3BucketExistsHealthCheck
    extends HealthCheckSupport
{
  private final S3Helper s3Helper;

  private final String bucket;

  public S3BucketExistsHealthCheck(final S3Helper s3Helper, final String bucket) {
    this.s3Helper = checkNotNull(s3Helper);
    this.bucket = checkNotNull(bucket);
  }

  @Override
  protected Result check() throws Exception {
    if (s3Helper.bucketExists(bucket)) {
      return Result.healthy();
    }
    return Result.unhealthy("Missing bucket: %s", bucket);
  }
}
