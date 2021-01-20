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

import java.nio.charset.Charset;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.sonatype.goodies.dropwizard.util.LoremIpsum;

import com.google.common.base.Strings;
import io.dropwizard.views.View;

import static com.google.common.base.Preconditions.checkState;

/**
 * Support for {@link View} implementations.
 *
 * @since 1.0.0
 */
public class ViewSupport
    extends View
{
  @Context
  private HttpServletRequest httpRequest;

  @Context
  private UriInfo uriInfo;

  public ViewSupport(final String templateName) {
    super(templateName);
  }

  public ViewSupport(final String templateName, @Nullable final Charset charset) {
    super(templateName, charset);
  }

  protected UriInfo getUriInfo() {
    checkState(uriInfo != null);
    return uriInfo;
  }

  /**
   * @since 1.2.0
   */
  protected Locale getLocale() {
    checkState(httpRequest != null);
    return httpRequest.getLocale();
  }

  /**
   * Strip trailing slash from given value.
   */
  private static String stripTrailingSlash(String value) {
    if (value.charAt(value.length() - 1) == '/') {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }

  /**
   * Extract application base-path; stripping off any trailing slash.
   */
  public String getBasePath() {
    return stripTrailingSlash(getUriInfo().getBaseUri().getPath());
  }

  /**
   * Extract application base-url; stripping off any trailing slash.
   */
  public String getBaseUrl() {
    return stripTrailingSlash(getUriInfo().getBaseUri().toString());
  }

  /**
   * Returns the current view path (stripping off any trailing slash), or {@code /} if at root.
   */
  public String getPath() {
    String path = getUriInfo().getPath();
    if (Strings.emptyToNull(path) == null) {
      path = "/";
    }
    else {
      path = stripTrailingSlash(path);
    }
    return path;
  }

  //
  // Place-holder text
  //

  /**
   * Generate some place-holder text.
   */
  public String loremIpsum(final int count) {
    return LoremIpsum.words(count);
  }

  /**
   * Generate some place-holder text.
   */
  public String loremIpsum(final int min, final int max) {
    return LoremIpsum.words(min, max);
  }
}
