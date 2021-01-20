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
package org.sonatype.goodies.dropwizard.app;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.common.version.VersionLoader;

import io.dropwizard.setup.Environment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link ApplicationMetadata}.
 *
 * @since ???
 */
@Named
@Singleton
public class ApplicationMetadataImpl
  implements ApplicationMetadata
{
  private final String name;

  private final VersionLoader versionLoader;

  @Inject
  public ApplicationMetadataImpl(final Environment environment, final VersionLoader loader) {
    checkNotNull(environment);
    this.name = environment.getName();
    this.versionLoader = checkNotNull(loader);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getVersion() {
    return versionLoader.getVersion();
  }

  @Override
  public String getBuildTimestamp() {
    return versionLoader.getTimestamp();
  }

  @Override
  public String getBuildTag() {
    return versionLoader.getTag();
  }

  @Override
  public String getBuildNotes() {
    return versionLoader.getNotes();
  }

  @Override
  public String toString() {
    return String.format("%s %s (%s; %s)",
        getName(),
        getVersion(),
        getBuildTimestamp(),
        getBuildTag()
    );
  }
}
