/*
 * Copyright (c) 2019-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
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
