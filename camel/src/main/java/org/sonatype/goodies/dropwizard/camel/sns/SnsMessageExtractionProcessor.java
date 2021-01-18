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
package org.sonatype.goodies.dropwizard.camel.sns;

import java.util.Map;

import org.sonatype.goodies.dropwizard.camel.ExchangeHelper2;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * Extract {@link #MESSAGE} from {@literal in} and replace as {@literal out} body.
 *
 * @since 1.3.0
 */
public class SnsMessageExtractionProcessor
    implements Processor
{
  /**
   * SNS {@literal Message} field name.
   */
  public static final String MESSAGE = "Message";

  @Override
  public void process(final Exchange exchange) throws Exception {
    Message out = ExchangeHelper2.copyIn(exchange);
    out.setBody(exchange.getIn().getMandatoryBody(Map.class).get(MESSAGE));
  }
}
