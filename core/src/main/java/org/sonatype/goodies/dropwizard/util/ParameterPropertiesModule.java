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
package org.sonatype.goodies.dropwizard.util;

import java.util.Map;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.eclipse.sisu.wire.ParameterKeys;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Binds Sisu {@link ParameterKeys#PROPERTIES}.
 *
 * Properties are used in {@link javax.inject.Named} annotations with {@code ${name:-default}} syntax.
 *
 * @since 1.0.2
 */
public class ParameterPropertiesModule
    implements Module
{
  private final Map<String,Object> properties;

  public ParameterPropertiesModule(final Map<String, Object> properties) {
    this.properties = checkNotNull(properties);
  }

  @Override
  public void configure(final Binder binder) {
    binder.bind(ParameterKeys.PROPERTIES).toInstance(properties);
  }
}
