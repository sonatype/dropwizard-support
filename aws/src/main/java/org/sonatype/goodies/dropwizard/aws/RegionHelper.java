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
package org.sonatype.goodies.dropwizard.aws;

import javax.annotation.Nullable;

import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.regions.Regions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS region helpers.
 *
 * @since 1.3.0
 */
public class RegionHelper
{
  private static final Logger log = LoggerFactory.getLogger(RegionHelper.class);

  private RegionHelper() {
    // empty
  }

  @Nullable
  public static Regions getCurrent() {
    Regions region = null;
    String name = new DefaultAwsRegionProviderChain().getRegion();
    if (name != null) {
      region = Regions.fromName(name);
    }
    log.trace("Current region: {}", region);
    return region;
  }
}
