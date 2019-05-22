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
package org.sonatype.goodies.dropwizard.selection;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.common.annotations.Beta;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Allows component to be given a set of group-names for component-selection.
 *
 * @since ???
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.PACKAGE})
@Beta
public @interface ComponentGroup
{
  /**
   * Special group-name to always enable component.
   */
  String ALWAYS = "ALWAYS";

  /**
   * Special group-name to never enable component.
   *
   * This only applies if type or package based selection has not matched.
   */
  String NEVER = "NEVER";

  /**
   * A set or ordered component group-names.
   *
   * First matching group-name wins.
   */
  String[] value();
}
