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
package org.sonatype.goodies.dropwizard.swagger2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import io.swagger.v3.core.filter.OpenAPISpecFilter;
import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.oas.models.OpenAPI;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link SpecFilter} helpers.
 *
 * @since ???
 */
public class SpecFilterHelper
{
  private SpecFilterHelper() {
    // empty
  }

  public static OpenAPI filter(final OpenAPI model,
                               final OpenAPISpecFilter filter,
                               @Nullable final UriInfo uriInfo,
                               @Nullable final HttpHeaders httpHeaders)
  {
    checkNotNull(model);
    checkNotNull(filter);

    return new SpecFilter().filter(
        model,
        filter,
        queryParams(uriInfo),
        cookies(httpHeaders),
        headers(httpHeaders)
    );
  }

  //
  // Helpers
  //

  private static Map<String, List<String>> queryParams(@Nullable final UriInfo uriInfo) {
    Map<String, List<String>> result = new HashMap<>();
    if (uriInfo != null) {
      MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
      if (params != null) {
        params.forEach(result::put);
      }
    }
    return result;
  }

  private static Map<String, String> cookies(@Nullable final HttpHeaders headers) {
    Map<String, String> result = new HashMap<>();
    if (headers != null) {
      headers.getCookies().forEach((name, cookie) -> result.put(name, cookie.getValue()));
    }
    return result;
  }

  private static Map<String, List<String>> headers(@Nullable final HttpHeaders headers) {
    Map<String, List<String>> result = new HashMap<>();
    if (headers != null) {
      headers.getRequestHeaders().forEach(result::put);
    }
    return result;
  }
}
