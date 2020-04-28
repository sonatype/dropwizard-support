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
package org.sonatype.goodies.dropwizard.camel;

import javax.annotation.Nullable;

import com.google.inject.TypeLiteral;
import org.apache.camel.Exchange;
import org.apache.camel.NoSuchPropertyException;
import org.apache.camel.support.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to access a named/typed {@link Exchange} property.
 *
 * @since 1.3.0
 */
public class ExchangePropertyAccess<T>
{
  private static final Logger log = LoggerFactory.getLogger(ExchangePropertyAccess.class);

  private final TypeLiteral<T> type = new TypeLiteral<T>() { };

  private final String key;

  public ExchangePropertyAccess(final String name) {
    checkNotNull(name);
    this.key = ExchangeHelper2.propertyKey(type.getRawType(), name);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public T get(final Exchange exchange) {
    checkNotNull(exchange);
    return (T) exchange.getProperty(key, type.getRawType());
  }

  @SuppressWarnings("unchecked")
  public T require(final Exchange exchange) throws NoSuchPropertyException {
    checkNotNull(exchange);
    return (T) ExchangeHelper.getMandatoryProperty(exchange, key, type.getRawType());
  }

  public void set(final Exchange exchange, final T value) {
    checkNotNull(exchange);
    checkNotNull(value);
    log.trace("Set: {}={}", key, value);
    exchange.setProperty(key, value);
  }
}
