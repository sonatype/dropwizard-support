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
package org.glassfish.jersey.test.inmemory;

import java.net.URI;

import com.sonatype.ossindex.dropwizard.jersey.BindableTestContainer;

import org.glassfish.jersey.server.ApplicationHandler;

/**
 * Custom {@link InMemoryConnector} provider that is exposed for out-of-package usage.
 *
 * @see BindableTestContainer
 * @since ???
 */
public class ExposedInMemoryConnectorProvider
    extends InMemoryConnector.Provider
{
  public ExposedInMemoryConnectorProvider(final URI baseUri, final ApplicationHandler appHandler) {
    super(baseUri, appHandler);
  }
}
