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
package com.sonatype.ossindex.dropwizard.client;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Adds {@link HttpHeaders#AUTHORIZATION} to requests.
 *
 * @since ???
 */
public class BasicAuthenticationRequestFilter
    implements ClientRequestFilter
{
  private final BasicAuthenticationConfiguration credentials;

  public BasicAuthenticationRequestFilter(final BasicAuthenticationConfiguration credentials) {
    this.credentials = checkNotNull(credentials);
  }

  @Override
  public void filter(final ClientRequestContext context) throws IOException {
    String token = String.format("%s:%s", credentials.getUsername(), credentials.getPassword());
    String value = "BASIC " + Base64.getEncoder().encodeToString(token.getBytes());
    MultivaluedMap<String, Object> headers = context.getHeaders();
    headers.putSingle(HttpHeaders.AUTHORIZATION, value);
  }
}
