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
package org.sonatype.goodies.dropwizard.views;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.app.ApplicationMetadata;
import org.sonatype.goodies.dropwizard.util.VersionLoader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to generate URL suffix for assets.
 *
 * @since 1.3.0
 */
@Named
@Singleton
public class UrlSuffixSupplier
    implements Supplier<String>
{
  private final ApplicationMetadata applicationMetadata;

  private volatile String suffix;

  @Inject
  public UrlSuffixSupplier(final ApplicationMetadata applicationMetadata) {
    this.applicationMetadata = checkNotNull(applicationMetadata);
  }

  @Override
  public String get() {
    if (suffix == null) {
      String v = applicationMetadata.getVersion();
      if (v.contains("SNAPSHOT") || VersionLoader.UNKNOWN.equals(v)) {
        v = String.valueOf(System.currentTimeMillis());
      }
      suffix = "_v=" + v;
    }
    return suffix;
  }
}
