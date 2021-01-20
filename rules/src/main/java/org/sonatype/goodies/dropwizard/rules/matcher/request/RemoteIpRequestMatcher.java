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
package org.sonatype.goodies.dropwizard.rules.matcher.request;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.sonatype.goodies.dropwizard.util.IpAddresses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Match {@link HttpServletRequest#getRemoteAddr() remote IP address}.
 *
 * @since 1.2.0
 */
@JsonTypeName(RemoteIpRequestMatcher.TYPE)
public class RemoteIpRequestMatcher
    implements RequestMatcher
{
  private static final Logger log = LoggerFactory.getLogger(RemoteIpRequestMatcher.class);

  public static final String TYPE = "remote-ip";

  private final IpAddresses addresses;

  public RemoteIpRequestMatcher(final IpAddresses addresses) {
    this.addresses = checkNotNull(addresses);
    log.debug("Addresses: {}", addresses);
  }

  @JsonCreator
  public RemoteIpRequestMatcher(@NotNull @JsonProperty("addresses") final List<String> addresses) {
    this(new IpAddresses(addresses));
  }

  public IpAddresses getAddresses() {
    return addresses;
  }

  @VisibleForTesting
  boolean match(final String address) {
    checkNotNull(address);
    return addresses.match(address);
  }

  @Override
  public boolean matches(final HttpServletRequest request) {
    return match(request.getRemoteAddr());
  }

  @Override
  public String toString() {
    return String.format("%s{%s}", TYPE, addresses);
  }
}
