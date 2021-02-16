/*
 * Copyright (c) 2020-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.config;

import javax.validation.constraints.NotNull;

import org.sonatype.goodies.dropwizard.aws.auth.AwsCredentialsProviderFactory;
import org.sonatype.goodies.dropwizard.aws.auth.DefaultAwsCredentialsProviderFactory;

import com.amazonaws.regions.Regions;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * AWS configuration.
 *
 * @since ???
 */
public class AwsConfiguration
{
  @JsonProperty
  private Regions region;

  public Regions getRegion() {
    return region;
  }

  public void setRegion(final Regions region) {
    this.region = region;
  }

  @NotNull
  @JsonProperty
  private AwsCredentialsProviderFactory credentialsProvider = new DefaultAwsCredentialsProviderFactory();

  public AwsCredentialsProviderFactory getCredentialsProvider() {
    return credentialsProvider;
  }

  public void setCredentialsProvider(final AwsCredentialsProviderFactory factory) {
    this.credentialsProvider = checkNotNull(factory);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("region", region)
        .add("credentialsProvider", credentialsProvider)
        .toString();
  }
}
