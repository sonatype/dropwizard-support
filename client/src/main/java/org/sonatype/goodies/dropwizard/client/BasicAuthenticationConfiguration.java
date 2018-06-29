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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Configuration for BASIC authentication credentials.
 *
 * @since ???
 */
public class BasicAuthenticationConfiguration
{
  @NotEmpty
  @JsonProperty
  private String username;

  @NotEmpty
  @JsonProperty
  private String password;

  public BasicAuthenticationConfiguration(final String username, final String password) {
    this.username = username;
    this.password = password;
  }

  public BasicAuthenticationConfiguration() {
    // empty
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }
}
