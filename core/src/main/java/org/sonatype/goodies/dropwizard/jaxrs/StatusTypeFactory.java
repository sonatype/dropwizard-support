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
package org.sonatype.goodies.dropwizard.jaxrs;

import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to create {@link StatusType} instances.
 *
 * @since 1.2.0
 */
public final class StatusTypeFactory
{
  private StatusTypeFactory() {
    // empty
  }

  public static StatusType create(final int code, final String reason) {
    checkArgument(code > 0);
    checkNotNull(reason);
    final Family family = Family.familyOf(code);
    return new StatusType() {
      @Override
      public int getStatusCode() {
        return code;
      }

      @Override
      public Family getFamily() {
        return family;
      }

      @Override
      public String getReasonPhrase() {
        return reason;
      }
    };
  }
}
