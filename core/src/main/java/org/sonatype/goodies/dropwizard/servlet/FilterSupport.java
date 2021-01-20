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
package org.sonatype.goodies.dropwizard.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.sonatype.goodies.dropwizard.util.Loggers;

import org.slf4j.Logger;

/**
 * Support for {@link Filter} implementations.
 *
 * @since 1.0.0
 */
public abstract class FilterSupport
    implements Filter
{
  protected final Logger log = Loggers.getLogger(getClass());

  @Override
  public void init(final FilterConfig config) throws ServletException {
    log.trace("Initialized");
  }

  @Override
  public void destroy() {
    log.trace("Destroyed");
  }
}
