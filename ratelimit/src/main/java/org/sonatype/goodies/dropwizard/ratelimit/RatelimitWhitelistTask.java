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
package org.sonatype.goodies.dropwizard.ratelimit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.dropwizard.task.IpAddressesTaskSupport;
import org.sonatype.goodies.dropwizard.util.IpAddresses;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link RatelimitService} whitelist task.
 *
 * @since 1.2.0
 */
@Named
@Singleton
public class RatelimitWhitelistTask
    extends IpAddressesTaskSupport
{
  private final RatelimitService ratelimitService;

  @Inject
  public RatelimitWhitelistTask(final RatelimitService ratelimitService) {
    super("ratelimit-whitelist");
    this.ratelimitService = checkNotNull(ratelimitService);
  }

  @Override
  protected IpAddresses getAddresses() {
    return ratelimitService.getWhitelist();
  }
}
