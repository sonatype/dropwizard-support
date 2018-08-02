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
package org.sonatype.goodies.dropwizard.client;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.ApplicationVersion;

import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Supplies {@link com.google.common.net.HttpHeaders#USER_AGENT} value.
 *
 * @see JerseyClientFactory
 * @since 1.0.0
 */
@Named
@Singleton
public class UserAgentSupplier
    implements Supplier<String>
{
  private static final Logger log = LoggerFactory.getLogger(UserAgentSupplier.class);

  private final String value;

  @Inject
  public UserAgentSupplier(final Environment environment, final ApplicationVersion version) {
    this(environment.getName(), version.getVersion());
  }

  @VisibleForTesting
  public UserAgentSupplier(final String product, final String version) {
    checkNotNull(product);
    checkNotNull(version);

    value = String.format("%s/%s (%s; %s; %s; %s)",
        product,
        version,
        System.getProperty("os.name"),
        System.getProperty("os.version"),
        System.getProperty("os.arch"),
        System.getProperty("java.version")
    );
    log.debug("User-agent: {}", value);
  }

  @Override
  public String get() {
    return value;
  }
}
