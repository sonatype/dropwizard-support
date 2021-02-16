/*
 * Copyright (c) 2019-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.sqs;

import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.guice.ModuleSupport;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.google.inject.Provides;

/**
 * AWS SQS module.
 *
 * @since ???
 */
public class SqsModule
    extends ModuleSupport
{
  @Provides
  @Singleton
  AmazonSQS getSqsClient(final Regions region,
                         final AWSCredentialsProvider credentialsProvider,
                         final ClientConfiguration clientConfiguration)
  {
    return AmazonSQSClientBuilder.standard()
        .withRegion(region)
        .withCredentials(credentialsProvider)
        .withClientConfiguration(clientConfiguration)
        .build();
  }
}
