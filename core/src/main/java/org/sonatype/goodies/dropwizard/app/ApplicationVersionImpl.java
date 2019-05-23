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

import org.sonatype.goodies.dropwizard.version.VersionLoader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link ApplicationVersion}.
 *
 * @since ???
 */
@Named
@Singleton
public class ApplicationVersionImpl
    implements ApplicationVersion
{
  private final VersionLoader loader;

  @Inject
  public ApplicationVersionImpl(final VersionLoader loader) {
    this.loader = checkNotNull(loader);
  }

  @Override
  public String getVersion() {
    return loader.getVersion();
  }

  @Override
  public String getBuildTimestamp() {
    return loader.getTimestamp();
  }

  @Override
  public String getBuildTag() {
    return loader.getTag();
  }

  @Override
  public String getBuildNotes() {
    return loader.getNotes();
  }

  @Override
  public String toString() {
    return loader.toString();
  }
}
