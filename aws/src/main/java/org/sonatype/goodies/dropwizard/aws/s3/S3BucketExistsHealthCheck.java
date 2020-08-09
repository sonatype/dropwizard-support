/*
 * Copyright (c) 2019-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
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
