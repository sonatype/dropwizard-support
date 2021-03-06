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

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fired to dispatch {@link WorkEvent}.
 *
 * Event-based equivalent of calling {@link WorkerService#dispatch(WorkEvent)}.
 *
 * @since ???
 */
public class DispatchWorkEvent
{
  private final WorkEvent event;

  public DispatchWorkEvent(final WorkEvent event) {
    this.event = checkNotNull(event);
  }

  public WorkEvent getEvent() {
    return event;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("event", event)
        .toString();
  }
}
