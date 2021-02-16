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
package org.sonatype.goodies.dropwizard.aws.config;

import java.util.Optional;

import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.aws.auth.AwsCredentialsProviderFactory;
import org.sonatype.goodies.dropwizard.guice.ModuleSupport;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.regions.Regions;
import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import io.dropwizard.util.Duration;

/**
 * AWS module.
 *
 * @since ???
 */
public class AwsModule
    extends ModuleSupport
{
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
