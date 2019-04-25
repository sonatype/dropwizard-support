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
package org.sonatype.goodies.dropwizard.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.dropwizard.configuration.ConfigurationSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Return first configuration that is not exceptional from given providers.
 *
 * @since ???
 */
public class FirstMatchConfigurationSourceProvider
    implements ConfigurationSourceProvider
{
  private final Logger log = LoggerFactory.getLogger(FirstMatchConfigurationSourceProvider.class);

  private final ConfigurationSourceProvider[] providers;

  public FirstMatchConfigurationSourceProvider(final ConfigurationSourceProvider... providers) {
    this.providers = checkNotNull(providers);
    checkArgument(providers.length != 0, "At least one provider must be given");
  }

  @Override
  public InputStream open(final String path) throws IOException {
    checkNotNull(path);

    List<Throwable> errors = new ArrayList<>(providers.length);
    for (ConfigurationSourceProvider provider : providers) {
      log.debug("Trying provider: {}", provider);
      try {
        return provider.open(path);
      }
      catch (Exception e) {
        log.debug("Ignoring provider: {}", provider, e);
        errors.add(e);
      }
    }

    IOException error = new IOException("No provider produced configuration input");
    errors.forEach(error::addSuppressed);
    throw error;
  }
}
