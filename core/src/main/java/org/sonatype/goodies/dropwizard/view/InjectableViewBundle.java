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
package org.sonatype.goodies.dropwizard.view;

import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.View;
import io.dropwizard.views.ViewConfigurable;
import io.dropwizard.views.ViewRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Injectable view bundle.
 *
 * Enables {@link View} implementations to receive injection from HK2.
 *
 * @since ???
 */
public class InjectableViewBundle<T extends Configuration>
    implements ConfiguredBundle<T>, ViewConfigurable<T>
{
  private static final Logger log = LoggerFactory.getLogger(InjectableViewBundle.class);

  private final Iterable<ViewRenderer> renderers;

  public InjectableViewBundle() {
    this(ServiceLoader.load(ViewRenderer.class));
  }

  public InjectableViewBundle(final Iterable<ViewRenderer> renderers) {
    this.renderers = ImmutableSet.copyOf(renderers);
  }

  @Override
  public Map<String, Map<String, String>> getViewConfiguration(final T configuration) {
    return ImmutableMap.of();
  }

  @Override
  public void initialize(final Bootstrap<?> bootstrap) {
    // empty
  }

  @Override
  public void run(final T configuration, final Environment environment) throws Exception {
    Map<String, Map<String, String>> options = getViewConfiguration(configuration);

    for (ViewRenderer renderer : renderers) {
      log.debug("Renderer: {} -> {}", renderer.getConfigurationKey(), renderer);
      Map<String, String> opts = options.get(renderer.getConfigurationKey());
      renderer.configure(opts != null ? opts : Collections.emptyMap());
    }

    // install injection aware view writer
    environment.jersey().register(new InjectableViewMessageBodyWriter(environment.metrics(), renderers));
  }
}