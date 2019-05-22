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
package org.sonatype.goodies.dropwizard.ratelimit;

import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;

import org.sonatype.goodies.dropwizard.app.ApplicationCustomizer;
import org.sonatype.goodies.dropwizard.app.ApplicationSupport;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link RatelimitService} application customizer.
 *
 * @since ???
 * @see RatelimitFilter
 */
public abstract class RatelimitCustomizer<T extends ApplicationSupport<C>, C extends Configuration>
    implements ApplicationCustomizer<T, C>
{
  private static final Logger log = LoggerFactory.getLogger(RatelimitCustomizer.class);

  protected abstract RatelimitConfiguration getRatelimitConfiguration(final C config);

  @Override
  public List<Module> modules(final C config, final Environment environment) {
    return ImmutableList.of(
        binder -> binder.bind(RatelimitConfiguration.class).toInstance(getRatelimitConfiguration(config))
    );
  }

  @Override
  public void customize(final T application,
                        final C config,
                        final Environment environment)
  {
    Class<? extends Filter> type = RatelimitFilter.class;
    Filter filter = application.getInstance(type);
    String urlPattern = "/*";

    FilterRegistration.Dynamic registration = environment.servlets().addFilter(type.getSimpleName(), filter);
    registration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, urlPattern);
    log.debug("Added filter: {} -> {}", type.getSimpleName(), urlPattern);
  }
}
