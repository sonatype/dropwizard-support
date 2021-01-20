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
package org.sonatype.goodies.dropwizard.app.er;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.slf4j.Logger;

/**
 * Network {@link EnvironmentReport.Section}.
 *
 * @since ???
 */
@JsonTypeName(NetworkSection.TYPE)
public class NetworkSection
  extends EnvironmentReport.Section
{
  public static final String TYPE = "network";

  public NetworkSection() {
    super(TYPE);
  }

  @Override
  public void render(final Logger logger) throws Exception {
    for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
      try {
        log(logger, "Network-interface: name={}, display-name={}, up={}, virtual={}, multicast={}, loopback={}, ptp={}, mtu={}, addresses={}",
            intf.getName(),
            intf.getDisplayName(),
            intf.isUp(),
            intf.isVirtual(),
            intf.supportsMulticast(),
            intf.isLoopback(),
            intf.isPointToPoint(),
            intf.getMTU(),
            Collections.list(intf.getInetAddresses()).stream()
                .map(InetAddress::toString)
                .collect(Collectors.joining(", "))
        );
      }
      catch (Exception e) {
        logger.warn("Failed to query network-interface: name={}", intf.getName(), e);
      }
    }
  }
}
