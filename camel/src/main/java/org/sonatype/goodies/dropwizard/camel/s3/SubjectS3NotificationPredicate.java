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

import org.sonatype.goodies.dropwizard.camel.sns.SnsNotificationSubjectPredicateSupport;

/**
 * Matches {@link Map} body which appears to be an S3 notification message.
 *
 * @since ???
 */
public class SubjectS3NotificationPredicate
    extends SnsNotificationSubjectPredicateSupport
{
  public static final String S3_NOTIFICATION_SUBJECT = "Amazon S3 Notification";

  public static final SubjectS3NotificationPredicate INSTANCE = new SubjectS3NotificationPredicate();

  public SubjectS3NotificationPredicate() {
    super(S3_NOTIFICATION_SUBJECT);
  }
}
