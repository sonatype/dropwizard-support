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
