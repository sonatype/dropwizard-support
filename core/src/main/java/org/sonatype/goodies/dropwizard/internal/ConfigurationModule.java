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
package org.sonatype.goodies.dropwizard.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.sonatype.goodies.dropwizard.ConfigurationSupport.Bind;

import com.google.inject.AbstractModule;
import io.dropwizard.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Adds bindings for {@link Configuration}.
 *
 * @since 1.0.0
 */
public class ConfigurationModule
    extends AbstractModule
{
  private static final Logger log = LoggerFactory.getLogger(ConfigurationModule.class);

  private final Configuration configuration;

  public ConfigurationModule(final Configuration configuration) {
    this.configuration = checkNotNull(configuration);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void configure() {
    bind((Class) configuration.getClass()).toInstance(configuration);

    try {
      autoBind(configuration);
    }
    catch (Exception e) {
      log.warn("Failed to bind exposed configuration", e);
    }
  }

  private void autoBind(final Object owner) throws Exception {
    log.trace("Auto-binding: {}", owner);

    Class<?> type = owner.getClass();
    for (Field field : type.getDeclaredFields()) {
      autoBind(field, owner);
    }
    for (Method method : type.getDeclaredMethods()) {
      autoBind(method, owner);
    }
  }

  @SuppressWarnings("unchecked")
  private void autoBind(final AccessibleObject member, final Object owner) throws Exception {
    log.trace("Auto-binding; member: {}", member);

    member.setAccessible(true);

    Bind binding = member.getAnnotation(Bind.class);
    if (binding != null) {
      Class<?> type;
      Object value;

      if (member instanceof Field) {
        Field field = (Field)member;
        type = field.getType();
        value = field.get(owner);
      }
      else if (member instanceof Method) {
        Method method = (Method)member;
        if (method.getParameterTypes().length != 0) {
          log.warn("Ignoring auto-bind method with arguments: {}", method);
          return;
        }
        else {
          type = method.getReturnType();
          if (type == Void.class) {
            log.warn("Ignoring auto-bind method with void-return: {}", method);
            return;
          }
          value = method.invoke(owner);
        }
      }
      else {
        throw new Error("Invalid member: " + member);
      }

      // optionally bind specific type if given
      if (binding.value() != Void.class) {
        type = binding.value();
      }

      log.trace("Binding: {} -> {}", type, value);
      bind((Class)type).toInstance(value);

      // maybe apply auto-binding to child value
      if (value != null) {
        if (value.getClass().getAnnotation(Bind.class) != null) {
          autoBind(value);
        }
      }
    }
  }
}
