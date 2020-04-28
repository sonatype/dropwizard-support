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
package org.sonatype.goodies.dropwizard.guice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.inject.Module;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Module Modules} helpers.
 *
 * @since 1.3.0
 */
public class ModulesHelper
{
  private ModulesHelper() {
    // empty
  }

  public static List<Module> concat(final Iterable<Module> basis, final Module... modules) {
    checkNotNull(basis);
    checkNotNull(modules);

    return Stream.concat(
        StreamSupport.stream(basis.spliterator(), false),
        Arrays.stream(modules)
    ).collect(Collectors.toList());
  }
}
