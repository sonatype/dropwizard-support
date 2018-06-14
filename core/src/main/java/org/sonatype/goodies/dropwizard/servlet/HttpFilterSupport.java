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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Adapt filter to {@link HttpServletRequest} and {@link HttpServletResponse}.
 *
 * @since ???
 */
public abstract class HttpFilterSupport
    extends FilterSupport
{
  /**
   * @see #filter(HttpServletRequest, HttpServletResponse, FilterChain)
   */
  @Override
  public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException
  {
    filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
  }

  protected abstract void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException;
}
