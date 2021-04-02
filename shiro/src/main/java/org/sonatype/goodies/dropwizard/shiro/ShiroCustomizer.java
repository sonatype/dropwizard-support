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
package org.sonatype.goodies.dropwizard.shiro;

import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.sonatype.goodies.dropwizard.app.ApplicationCustomizer;
import org.sonatype.goodies.dropwizard.app.ApplicationSupport;
import org.sonatype.goodies.dropwizard.shiro.mdc.MdcUserScopeFilter;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import io.dropwizard.Configuration;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.setup.Environment;
import org.apache.shiro.guice.aop.ShiroAopModule2;
import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.apache.shiro.web.jaxrs.SubjectPrincipalRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// HACK: this is far from ideal, but for now just to get something a bit more common in place

/**
 * Security customizer.
 *
 * @since 1.3.0
 */
@Beta
public class ShiroCustomizer<T extends ApplicationSupport<C>, C extends Configuration>
    implements ApplicationCustomizer<T, C>
{
  private static final Logger log = LoggerFactory.getLogger(ShiroCustomizer.class);

  @Override
  public List<Module> modules(final C config, final Environment environment) {
    return ImmutableList.of(
        createSecurityModule(),
        // must come after SecurityModule
        new ShiroAopModule2()
    );
  }

  protected ShiroModuleSupport createSecurityModule() {
    return new ShiroModuleSupport();
  }

  @Override
  public void customize(final T application,
                        final C config,
                        final Environment environment)
  {
    addFilter(application, environment, GuiceShiroFilter.class, false, "/*");
    addFilter(application, environment, MdcUserScopeFilter.class, true, "/*");

    DropwizardResourceConfig rconfig = environment.jersey().getResourceConfig();
    rconfig.register(SubjectPrincipalRequestFilter.class);
    rconfig.register(ShiroAopFeature.class);
  }

  private void addFilter(final T application,
                         final Environment environment,
                         final Class<? extends Filter> type,
                         final boolean matchAfter,
                         final String urlPattern)
  {
    Filter filter = application.getInstance(type);
    environment.servlets()
        .addFilter(type.getSimpleName(), filter)
        .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), matchAfter, urlPattern);
    log.debug("Added filter: {} -> {}", type.getSimpleName(), urlPattern);
  }
}
