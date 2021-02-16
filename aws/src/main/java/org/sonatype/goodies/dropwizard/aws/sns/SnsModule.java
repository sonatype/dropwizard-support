/*
 * Copyright (c) 2019-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.sns;

import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.guice.ModuleSupport;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.google.inject.Provides;

/**
 * AWS SNS module.
 *
 * @since ???
 */
public class SnsModule
    extends ModuleSupport
{
  @Provides
  @Singleton
  AmazonSNS getSnsClient(final Regions region,
                         final AWSCredentialsProvider credentialsProvider,
                         final ClientConfiguration clientConfiguration)
  {
    return AmazonSNSClientBuilder.standard()
        .withRegion(region)
        .withCredentials(credentialsProvider)
        .withClientConfiguration(clientConfiguration)
        .build();
  }
}
