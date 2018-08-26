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
package org.sonatype.goodies.dropwizard.testsupport;

import java.util.function.Consumer;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import com.google.common.net.HttpHeaders;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * JAX-RS {@link Response} assert helpers.
 *
 * @since 1.0.0
 */
public final class ResponseAssert
{
  private ResponseAssert() {
    // empty
  }

  /**
   * @since ???
   */
  public static void assertStatus(final Response response, final StatusType status) {
    assertThat(response.getStatus(), is(status.getStatusCode()));
  }

  /**
   * @since 1.0.2
   */
  public static void assertStatus(final Response response, final int status) {
    assertThat(response.getStatus(), is(status));
  }

  /**
   * @since ???
   */
  public static void assertHeaderValue(final Response response, final String name, final String value) {
    assertThat(response.getHeaderString(name), is(value));
  }

  public static void assertContentType(final Response response, final String mediaType) {
    assertHeaderValue(response, HttpHeaders.CONTENT_TYPE, mediaType);
  }

  /**
   * @since 1.0.2
   */
  public static <T> void assertEntity(final Response response, final Class<T> type, final Consumer<T> validator) {
    assertThat(response.hasEntity(), is(true));
    T entity = response.readEntity(type);
    assertThat(entity, notNullValue(type));
    validator.accept(entity);
  }
}
