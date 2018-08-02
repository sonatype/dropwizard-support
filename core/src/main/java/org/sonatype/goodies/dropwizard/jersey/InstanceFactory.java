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
package org.sonatype.goodies.dropwizard.jersey;

import org.glassfish.hk2.api.Factory;

/**
 * Factory to wrap an instance.
 *
 * @see BinderSupport#bindInstance(Class, Object)
 * @since 1.0.0
 */
public class InstanceFactory<T>
    implements Factory<T>
{
  private final T instance;

  public InstanceFactory(final T instance) {
    this.instance = instance;
  }

  @Override
  public T provide() {
    return instance;
  }

  @Override
  public void dispose(final T instance) {
    // empty
  }
}
