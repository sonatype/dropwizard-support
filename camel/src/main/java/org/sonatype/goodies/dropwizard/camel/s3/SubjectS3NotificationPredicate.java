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
package org.sonatype.goodies.dropwizard.camel.s3;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matches {@link Map} body which appears to be an S3 notification message.
 *
 * @since ???
 */
public class SubjectS3NotificationPredicate
    implements Predicate
{
  public static final SubjectS3NotificationPredicate INSTANCE = new SubjectS3NotificationPredicate();

  @Override
  public boolean matches(final Exchange exchange) {
    checkNotNull(exchange);

    Map body = exchange.getIn().getBody(Map.class);
    return body != null &&
        "Notification".equals(body.get("Type")) &&
        "Amazon S3 Notification".equals(body.get("Subject"));
  }
}
