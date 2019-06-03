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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.dropwizard.Configuration;
import org.apache.commons.lang3.reflect.FieldUtils;
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

  @Override
  protected void configure() {
    // bind original configuration
    bind(configuration.getClass(), null, configuration);

    // expose configuration member bindings
    expose(configuration);

    // bind named attachments; and expose any bindings
    if (configuration instanceof ConfigurationAttachmentAware) {
      Map<String, ConfigurationAttachment> attachments = ((ConfigurationAttachmentAware)configuration).getConfigurationAttachments();
      for (Map.Entry<String,ConfigurationAttachment> entry : attachments.entrySet()) {
        ConfigurationAttachment value = entry.getValue();
        bind(value.getClass(), entry.getKey(), value);
        expose(value);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void bind(final Class type, @Nullable final String name, final Object value) {
    if (name != null && !Bind.DEFAULT_NAME.equals(name)) {
      log.trace("Binding: {}({}) -> {}", type.getCanonicalName(), name, value);
      bind(type).annotatedWith(Names.named(name)).toInstance(value);
    }
    else {
      log.trace("Binding: {} -> {}", type.getCanonicalName(), value);
      bind(type).toInstance(value);
    }
  }

  //
  // Component binding exposure
  //

  /**
   * Attempt to expose bindings for given object.
   */
  private void expose(final Object owner) {
    log.trace("Exposing bindings: {}", owner);

    Class<?> type = owner.getClass();

    try {
      // attempt to expose all fields
      for (Field field : FieldUtils.getAllFields(type)) {
        expose(owner, field);
      }

      // attempt to expose all methods
      for (Method method : type.getMethods()) {
        expose(owner, method);
      }
    }
    catch (Exception e) {
      Throwables.throwIfUnchecked(e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Attempt to expose bindings for given member.
   */
  private void expose(final Object owner, final AccessibleObject member) throws Exception {
    member.setAccessible(true);
    Bind bind = member.getAnnotation(Bind.class);

    // skip if no binding for member
    if (bind == null) {
      return;
    }

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
        log.warn("Ignoring exposed method with arguments: {}", method);
        return;
      }
      else {
        type = method.getReturnType();
        if (type == Void.class) {
          log.warn("Ignoring exposed method with void-return: {}", method);
          return;
        }
        value = method.invoke(owner);
      }
    }
    else {
      throw new Error("Invalid member: " + member);
    }

    // optionally bind specific type if given
    if (bind.type() != Bind.DEFAULT_TYPE) {
      type = bind.type();
    }

    // can only bind non-null value
    if (value != null) {
      bind(type, bind.name(), value);

      // maybe apply auto-binding to child value
      expose(value);
    }
  }
}
