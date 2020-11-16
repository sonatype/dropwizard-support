/*
<<<<<<< HEAD
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
=======
 * Copyright (c) 2019-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
>>>>>>> master
 */
package org.sonatype.goodies.dropwizard.health;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response.Status.Family;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Client} GET request is {@link Family#SUCCESSFUL} health-check.
 *
 * @since ???
 */
public class GetSuccessfulHealthCheck
    extends HealthCheckSupport
{
  private final Client client;

  private final URI url;

  private final String path;

  public GetSuccessfulHealthCheck(final Client client,
                                  final URI url,
                                  final String path)
  {
    this.client = checkNotNull(client);
    this.url = checkNotNull(url);
    this.path = path;
  }

  @Override
  protected Result check() throws Exception {
    WebTarget target = client.target(url).path(path);
    return HealthCheckHelper.checkStatus(target, Family.SUCCESSFUL);
  }
}
