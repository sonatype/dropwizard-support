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
package org.sonatype.goodies.dropwizard.views;

import java.util.Collections;
import java.util.Map;

import org.sonatype.goodies.dropwizard.app.ApplicationCustomizer;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;

/**
 * View {@link ApplicationCustomizer}.
 *
 * @since ???
 */
public class ViewCustomizer
    implements ApplicationCustomizer
{
  @Override
  public void initialize(final Bootstrap bootstrap) throws Exception {
    bootstrap.addBundle(new InjectableViewBundle() {
      @Override
      public Map<String, Map<String, String>> getViewConfiguration(final Configuration configuration) {
        if (configuration instanceof ViewConfigurationAware) {
          ViewConfiguration vconfig = ((ViewConfigurationAware)configuration).getViewConfiguration();
          return vconfig.getRenderersConfiguration();
        }
        return Collections.emptyMap();
      }
    });
  }
}
