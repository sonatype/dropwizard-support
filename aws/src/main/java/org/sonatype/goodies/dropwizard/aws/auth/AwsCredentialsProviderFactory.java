/*
 * Copyright (c) 2020-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.auth;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.dropwizard.jackson.Discoverable;

/**
 * {@link AWSCredentialsProvider} factory.
 *
 * @since ???
 */
@JsonTypeInfo(use = Id.NAME, property = "type")
public interface AwsCredentialsProviderFactory
    extends Discoverable
{
  AWSCredentialsProvider create();
}
