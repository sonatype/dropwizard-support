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
package org.sonatype.goodies.dropwizard.version;

import org.sonatype.goodies.dropwizard.app.ApplicationSupport;
import org.sonatype.goodies.dropwizard.util.VersionLoader;

import com.google.inject.AbstractModule;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support application version integration.
 *
 * @since ???
 * @see VersionLoader
 */
public class VersionModule
    extends AbstractModule
{
  private final ApplicationSupport<?> application;

  public VersionModule(final ApplicationSupport<?> application) {
    this.application = checkNotNull(application);
  }

  @Override
  protected void configure() {
    bind(VersionLoader.class).toInstance(new VersionLoader(application.getClass()));
  }
}
