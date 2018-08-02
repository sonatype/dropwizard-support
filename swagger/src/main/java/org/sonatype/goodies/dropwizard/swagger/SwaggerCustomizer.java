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
package org.sonatype.goodies.dropwizard.swagger;

import java.util.List;

import org.sonatype.goodies.dropwizard.ApplicationCustomizer;
import org.sonatype.goodies.dropwizard.ApplicationSupport;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Swagger application customizer.
 *
 * @since 1.0.0
 */
public abstract class SwaggerCustomizer<T extends ApplicationSupport<C>, C extends Configuration>
    implements ApplicationCustomizer<T, C>
{
  protected abstract SwaggerConfiguration getSwaggerConfiguration(final C config);

  @Override
  public void initialize(final Bootstrap<C> bootstrap) {
    bootstrap.addBundle(new SwaggerBundle<>());
  }

  @Override
  public List<Module> modules(final C config, final Environment environment) {
    return ImmutableList.of(
        new SwaggerModule(getSwaggerConfiguration(config))
    );
  }
}
