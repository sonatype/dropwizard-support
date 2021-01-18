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
package org.sonatype.goodies.dropwizard.worker;

import java.io.Serializable;

import org.sonatype.goodies.dropwizard.camel.sns.SnsNotificationSubjectPredicateSupport;
import org.sonatype.goodies.dropwizard.worker.internal.JsonDataFormatSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.camel.Predicate;
import org.apache.camel.spi.DataFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link WorkEvent} envelope.
 *
 * @since ???
 */
public class WorkEventEnvelope
    implements Serializable
{
  private static final long serialVersionUID = 1L;

  public static final DataFormat DATA_FORMAT = new JsonDataFormatSupport(WorkEventEnvelope.class);

  // TODO: consider exposing subject-details as component to allow application to control
  public static final String SUBJECT = WorkEventEnvelope.class.getSimpleName();

  public static final Predicate SUBJECT_PREDICATE = new SnsNotificationSubjectPredicateSupport(SUBJECT);

  private final WorkEvent payload;

  @JsonCreator
  public WorkEventEnvelope(@JsonProperty("payload") final WorkEvent payload) {
    this.payload = checkNotNull(payload);
  }

  public WorkEvent getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("payload", payload)
        .toString();
  }
}
