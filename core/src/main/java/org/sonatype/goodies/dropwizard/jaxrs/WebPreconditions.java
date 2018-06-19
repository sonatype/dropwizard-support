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
package org.sonatype.goodies.dropwizard.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * Web preconditions.
 *
 * Throws {@link WebApplicationException} appropriate for precondition failure.
 *
 * @since ???
 */
public final class WebPreconditions
{
  private WebPreconditions() {
    // empty
  }

  /**
   * Throws {@link WebApplicationException} with {@link Status#BAD_REQUEST} if expression is false.
   */
  public static void checkRequest(final boolean expression, final String message, final Object... params) {
    if (!expression) {
      throw new WebApplicationException(String.format(message, params), Status.BAD_REQUEST);
    }
  }

  /**
   * Throws {@link WebApplicationException} with {@link Status#BAD_REQUEST} if expression is false.
   */
  public static  void checkRequest(final boolean expression, final Object message) {
    if (!expression) {
      throw new WebApplicationException(String.valueOf(message), Status.BAD_REQUEST);
    }
  }

  /**
   * Throws {@link WebApplicationException} with {@link Status#BAD_REQUEST} if expression is false.
   */
  public static  void checkRequest(final boolean expression) {
    if (!expression) {
      throw new WebApplicationException(Status.BAD_REQUEST);
    }
  }
}
