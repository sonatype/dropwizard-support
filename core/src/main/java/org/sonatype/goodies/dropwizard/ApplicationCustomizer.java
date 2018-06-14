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
package org.sonatype.goodies.dropwizard;

import java.util.Collections;
import java.util.List;

import com.google.inject.Module;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Allows various customization of application configuration and environment.
 *
 * @since ???
 * @see ApplicationSupport#addCustomizer(ApplicationCustomizer[])
 */
public interface ApplicationCustomizer<T extends ApplicationSupport<C>, C extends Configuration>
{
  /**
   * Contribute to bootstrap.
   *
   * @throws Exception  Any exception from customization will cause the server to abort.
   */
  default void initialize(final Bootstrap<C> bootstrap) throws Exception {
    // empty
  }

  /**
   * Contribute modules for injection.
   *
   * This is called before the container environment is constructed.
   *
   * @throws Exception  Any exception from customization will cause the server to abort.
   */
  default List<Module> modules(final C config, final Environment environment) throws Exception {
    return Collections.emptyList();
  }

  /**
   * Applied after injection is ready.
   *
   * @throws Exception  Any exception from customization will cause the server to abort.
   */
  default void customize(final T application, final C config, final Environment environment) throws Exception {
    // empty
  }
}
