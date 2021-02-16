/*
 * Copyright (c) 2019-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.s3;

import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.guice.ModuleSupport;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.inject.Provides;

/**
 * AWS S3 module.
 *
 * @since ???
 */
public class S3Module
    extends ModuleSupport
{
  @Provides
  @Singleton
  AmazonS3 getAmazonS3(final Regions region,
                       final AWSCredentialsProvider credentialsProvider,
                       final ClientConfiguration clientConfiguration)
  {
    return AmazonS3ClientBuilder.standard()
        .withRegion(region)
        .withCredentials(credentialsProvider)
        .withClientConfiguration(clientConfiguration)
        .build();
  }
}
