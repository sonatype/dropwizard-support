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
package org.sonatype.goodies.dropwizard.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.common.annotations.Beta;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker for configuration to expose as Sisu binding.
 *
 * @since 1.2.0
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Beta
public @interface Bind
{
  /**
   * Default {@link #type()} for bindings w/o explicit type.
   */
  Class<?> DEFAULT_TYPE = Void.class;

  /**
   * Default {@link #name()} for bindings w/o explicit name.
   */
  String DEFAULT_NAME = "__DEFAULT__";

  /**
   * Configure explicit binding type.
   */
  Class<?> type() default Void.class;

  /**
   * Configure explicit binding name.
   */
  String name() default DEFAULT_NAME;
}
