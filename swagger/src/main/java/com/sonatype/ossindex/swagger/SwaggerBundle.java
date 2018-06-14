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
package com.sonatype.ossindex.swagger;

import com.sonatype.ossindex.dropwizard.assets.ClassContextAssetsBundle;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Swagger bundle.
 *
 * @see SwaggerModule
 * @since ???
 */
public class SwaggerBundle<T extends Configuration>
    implements ConfiguredBundle<T>
{
  private static final String SWAGGER_VERSION = "3.13.4";

  @Override
  public void initialize(final Bootstrap<?> bootstrap) {
    // enable asset support; use class-context to properly find resources
    bootstrap.addBundle(
        new ClassContextAssetsBundle(getClass(), "/META-INF/resources/webjars/swagger-ui/" + SWAGGER_VERSION,
            "/assets/swagger", "index.html", "swagger"));
  }

  @Override
  public void run(final T configuration, final Environment environment) throws Exception {
    // empty
  }
}
