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

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Exchange} helpers.
 *
 * @since 1.3.0
 */
public class ExchangeHelper2
{
  private static final Logger log = LoggerFactory.getLogger(ExchangeHelper2.class);

  /**
   * Copy {@literal in} message to {@literal out} message and return {@literal out}.
   */
  public static Message copyIn(final Exchange exchange) {
    checkNotNull(exchange);
    Message in = exchange.getIn();
    Message out = exchange.getOut();
    out.copyFrom(in);
    return out;
  }

  public static String propertyKey(final Class<?> type, final String name) {
    return String.format("%s_%s", type.getSimpleName(), name);
  }
}
