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
package org.sonatype.goodies.dropwizard.testsupport;

import java.net.URI;

import javax.annotation.Nullable;
import javax.ws.rs.client.WebTarget;

import org.sonatype.goodies.dropwizard.ApplicationSupport;
import org.sonatype.goodies.dropwizard.client.endpoint.EndpointFactory;

import io.dropwizard.Configuration;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;

import static com.google.common.base.Preconditions.checkNotNull;

// NOTE: Groovy does not like generic types here, so using Java

/**
 * Support rule for endpoint tests.
 *
 * @since 1.0.0
 */
public class EndpointSupportRule<T extends ApplicationSupport<C>, C extends Configuration>
    extends DropwizardAppRule<C>
{
  public EndpointSupportRule(final Class<? extends ApplicationSupport<C>> type,
                             final String configPath,
                             final ConfigOverride... configOverrides)
  {
    super(type, configPath, configOverrides);
  }

  public URI getBaseUrl() {
    // trailing "/" is important
    return URI.create(String.format("http://localhost:%s/", getLocalPort()));
  }

  public URI getAdminUrl() {
    // trailing "/" is important
    return URI.create(String.format("http://localhost:%s/", getAdminPort()));
  }

  public <E> E endpoint(final Class<E> type, @Nullable final String path) {
    checkNotNull(type);
    WebTarget target = client().target(getBaseUrl());
    if (path != null) {
      target = target.path(path);
    }
    return EndpointFactory.create(type, target);
  }

  public <E> E endpoint(final Class<E> type) {
    return endpoint(type, null);
  }
}
