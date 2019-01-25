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
package org.sonatype.goodies.dropwizard.ratelimit;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.sonatype.goodies.dropwizard.ratelimit.RatelimitConfiguration.RequestStrategy;

import com.google.common.base.MoreObjects;
import com.google.common.base.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link RatelimitFilter} tracker.
 *
 * @since ???
 */
public class RatelimitTracker
{
  private static final Logger log = LoggerFactory.getLogger(RatelimitTracker.class);

  public static class Identifier
  {
    public enum Type
    {
      /**
       * Tracked by username.
       */
      USERNAME,

      /**
       * Tracked by remote-ip.
       */
      REMOTE_IP
    }

    public final Type type;

    public final String value;

    public Identifier(final Type type, final String value) {
      this.type = checkNotNull(type);
      this.value = checkNotNull(value);
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Identifier that = (Identifier) o;
      return type == that.type &&
          Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, value);
    }

    @Override
    public String toString() {
      return String.format("%s:%s", type, value);
    }
  }

  private final Identifier id;

  private final Map<RequestStrategy, LeakyBucket> buckets = new ConcurrentHashMap<>();

  private long timestamp;

  public RatelimitTracker(final Identifier id) {
    this.id = checkNotNull(id);
  }

  public Identifier getId() {
    return id;
  }

  public Map<RequestStrategy, LeakyBucket> getBuckets() {
    return buckets;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void touch(final Ticker ticker) {
    timestamp = ticker.read();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("timestamp", timestamp)
        .toString();
  }
}
