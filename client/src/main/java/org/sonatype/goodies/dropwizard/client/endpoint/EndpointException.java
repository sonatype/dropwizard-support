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
package org.sonatype.goodies.dropwizard.client.endpoint;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Endpoint exception.
 *
 * Thrown to wrap {@link ClientErrorException} and prevent propagation of JAX-RS exception outside of scope.
 *
 * @since 1.0.0
 */
public class EndpointException
  extends RuntimeException
{
  public EndpointException(final ClientErrorException cause) {
    super(cause);
  }

  @Override
  public ClientErrorException getCause() {
    return (ClientErrorException)super.getCause();
  }

  public Response getResponse() {
    return getCause().getResponse();
  }

  /**
   * Helper to check if response carries given status.
   *
   * @since 1.0.2
   */
  public boolean isStatus(final Status status) {
    checkNotNull(status);
    return getResponse().getStatus() == status.getStatusCode();
  }
}
