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
package org.sonatype.goodies.dropwizard.shiro.subject;

import java.util.concurrent.Callable;

import org.sonatype.goodies.dropwizard.shiro.mdc.MdcUserScope;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Adapter to execute given {@link Callable} as {@link SystemSubject}.
 *
 * @since 1.0.0
 */
public class SystemCallable<V>
  implements Callable<V>
{
  private final Callable<V> delegete;

  public SystemCallable(final Callable<V> delegete) {
    this.delegete = checkNotNull(delegete);
  }

  @Override
  public V call() throws Exception {
    SystemSubject subject = SystemSubject.get();
    try (MdcUserScope scope = MdcUserScope.forSubject(subject)) {
      return subject.execute(delegete);
    }
  }
}
