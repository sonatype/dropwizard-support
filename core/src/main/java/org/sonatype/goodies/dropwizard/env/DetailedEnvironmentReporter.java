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
package org.sonatype.goodies.dropwizard.env;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.stream.Collectors;

import org.slf4j.Logger;

/**
 * Detailed {@link EnvironmentReporter}.
 *
 * @since ???
 */
public class DetailedEnvironmentReporter
  extends BasicEnvironmentReporter
{
  @Override
  public void report(final Logger logger) throws Exception {
    super.report(logger);

    Runtime runtime = Runtime.getRuntime();
    logger.info("CPU; processors={}", runtime.availableProcessors());
    logger.info("Memory; free={}, total={}, max={}",
        runtime.freeMemory(),
        runtime.totalMemory(),
        runtime.maxMemory()
    );

    for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
      logger.info("File-store; name={}, type={}, total={}, usable={}, unallocated={}, read-only={}",
          fileStore.name(),
          fileStore.type(),
          fileStore.getTotalSpace(),
          fileStore.getUsableSpace(),
          fileStore.getUnallocatedSpace(),
          fileStore.isReadOnly()
      );
    }

    for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
      logger.info("Network-interface: name={}, display-name={}, up={}, virtual={}, multicast={}, loopback={}, ptp={}, mtu={}, addresses={}",
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
  }
}
