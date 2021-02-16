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
