/*
 * Copyright (c) 2020-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.auth;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * {@link DefaultAWSCredentialsProviderChain} factory.
 *
 * @since ???
 */
@JsonTypeName("default")
public class DefaultAwsCredentialsProviderFactory
  implements AwsCredentialsProviderFactory
{
  @Override
  public AWSCredentialsProvider create() {
    return DefaultAWSCredentialsProviderChain.getInstance();
  }
}
