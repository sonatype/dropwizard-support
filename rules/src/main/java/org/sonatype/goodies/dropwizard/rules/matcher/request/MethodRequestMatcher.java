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

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.sonatype.goodies.dropwizard.util.MoreStrings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Match {@link HttpServletRequest#getMethod() method}.
 *
 * @since ???
 */
@JsonTypeName("method")
public class MethodRequestMatcher
    implements RequestMatcher
{
  private final Set<String> methods = new HashSet<>();

  @JsonCreator
  public MethodRequestMatcher(@NotNull @JsonProperty("methods") final Set<String> methods) {
    checkNotNull(methods);
    for (String method : methods) {
      this.methods.add(MoreStrings.upper(method));
    }
  }

  @Override
  public boolean match(final HttpServletRequest request) {
    return methods.contains(MoreStrings.upper(request.getMethod()));
  }

  @Override
  public String toString() {
    return String.format("method{%s}", methods);
  }
}
