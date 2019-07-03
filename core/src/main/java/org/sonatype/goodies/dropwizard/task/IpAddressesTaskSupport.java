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
package org.sonatype.goodies.dropwizard.task;

import java.io.PrintWriter;

import org.sonatype.goodies.dropwizard.util.IpAddresses;

/**
 * Support for tasks to manage {@link IpAddresses}.
 *
 * @since 1.2.0
 */
public abstract class IpAddressesTaskSupport
    extends TaskSupport
{
  public IpAddressesTaskSupport(final String name) {
    super(name);
  }

  protected abstract IpAddresses getAddresses();

  @Override
  protected void doExecute(final Parameters parameters, final PrintWriter output) throws Exception {
    String mode = parameters.value("mode", "show").toLowerCase();
    log.debug("Mode: {}", mode);

    IpAddresses addresses = getAddresses();
    switch (mode) {
      case "add": {
        String address = parameters.require("address");
        if (address != null) {
          if (addresses.addAddress(address)) {
            output.println("Added");
          }
        }
        break;
      }

      case "remove": {
        String address = parameters.require("address");
        if (address != null) {
          if (addresses.removeAddress(address)) {
            output.println("Removed");
          }
        }
        break;
      }

      case "clear":
        addresses.clear();
        output.println("Cleared");
        break;

      case "show":
        for (String address : addresses) {
          output.println(address);
        }
        break;

      default:
        throw new RuntimeException("Unknown mode: " + mode);
    }
  }
}
