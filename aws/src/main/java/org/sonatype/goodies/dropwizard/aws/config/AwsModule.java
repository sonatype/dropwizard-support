/*
 * Copyright (c) 2020-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.config;

import java.util.Optional;

import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.aws.auth.AwsCredentialsProviderFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.regions.Regions;
import com.google.common.primitives.Ints;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.dropwizard.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS module.
 *
 * @since ???
 */
public class AwsModule
    extends AbstractModule
{
  private static final Logger log = LoggerFactory.getLogger(AwsModule.class);

  @Provides
  @Singleton
  AwsRegionProvider getRegionProvider() {
    return new DefaultAwsRegionProviderChain();
  }

  @Provides
  @Singleton
  Regions getRegion(final AwsConfiguration configuration, final AwsRegionProvider regionProvider) {
    Regions region = configuration.getRegion();
    if (region == null) {
      // NOTE: when loading region from profile; the profile name value must be prefixed with "profile "
      region = Optional.ofNullable(regionProvider.getRegion())
          .map(Regions::fromName)
          .orElseThrow(() -> new RuntimeException("Unable to detect AWS region"));
    }
    log.info("Region: {}", region);
    return region;
  }

  @Provides
  @Singleton
  AWSCredentialsProvider getCredentialsProvider(final AwsConfiguration configuration) {
    AwsCredentialsProviderFactory factory = configuration.getCredentialsProvider();
    log.info("Credentials-provider factory: {}", factory);
    AWSCredentialsProvider provider = factory.create();
    log.info("Credentials-provider: {}", provider);
    return provider;
  }

  @Provides
  @Singleton
  ClientConfiguration getClientConfiguration() {
    ClientConfiguration config = new ClientConfiguration();
    Duration socketTimeout = Duration.minutes(5);
    config.setSocketTimeout(Ints.checkedCast(socketTimeout.toMilliseconds()));
    return config;
  }
}
