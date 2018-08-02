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
package org.sonatype.goodies.dropwizard.client;

import java.io.IOException;
import java.util.Collections;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Adds {@link HttpHeaders#USER_AGENT} to requests.
 *
 * @since 1.0.0
 */
public class UserAgentRequestFilter
    implements ClientRequestFilter
{
  private final UserAgentSupplier userAgent;

  public UserAgentRequestFilter(final UserAgentSupplier userAgent) {
    this.userAgent = checkNotNull(userAgent);
  }

  @Override
  public void filter(final ClientRequestContext context) throws IOException {
    MultivaluedMap<String, Object> headers = context.getHeaders();
    headers.putIfAbsent(HttpHeaders.USER_AGENT, Collections.singletonList(userAgent.get()));
  }
}
