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

import javax.annotation.Nullable;
import javax.ws.rs.client.Client;

import io.dropwizard.client.JerseyClientBuilder;

/**
 * Factory to produce Jersey {@link Client} instances from {@link ExtJerseyClientConfiguration}.
 *
 * @since 1.0.0
 */
public interface JerseyClientFactory
{
  /**
   * Allow customizer of client-builder.
   */
  interface ClientCustomizer
  {
    void customizer(JerseyClientBuilder builder);
  }

  /**
   * Create client with optional customizer.
   */
  Client create(ExtJerseyClientConfiguration config, String name, @Nullable ClientCustomizer customizer);

  /**
   * Create client.
   *
   * @see #create(ExtJerseyClientConfiguration, String, ClientCustomizer)
   */
  Client create(ExtJerseyClientConfiguration config, String name);
}
