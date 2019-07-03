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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.dropwizard.util.Duration;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link RatelimitService} configuration.
 *
 * @since 1.2.0
 */
public class RatelimitConfiguration
{
  /**
   * List of IP addresses that will be whitelisted.
   */
  @NotNull
  @JsonProperty
  private List<String> whitelist = new LinkedList<>();

  public List<String> getWhitelist() {
    return whitelist;
  }

  public void setWhitelist(@NotNull final List<String> whitelist) {
    this.whitelist = checkNotNull(whitelist);
  }

  /**
   * Configuration of the rate a bucket drains.
   */
  public static class DrainRate
  {
    // TODO: would like to express this as 'foo: count/period' in YAML configuration

    @Min(1)
    @JsonProperty
    private int count;

    @NotNull
    @JsonProperty
    private Duration period;

    public int getCount() {
      return count;
    }

    public void setCount(final int count) {
      this.count = count;
    }

    public Duration getPeriod() {
      return period;
    }

    public void setPeriod(@NotNull final Duration period) {
      this.period = checkNotNull(period);
    }

    @Override
    public String toString() {
      return String.format("%s/%s", count, period);
    }
  }

  /**
   * Bucket configuration.
   */
  public static class Bucket
  {
    @Min(1)
    @JsonProperty
    private int capacity;

    @NotNull
    @JsonProperty
    private DrainRate drainRate;

    public int getCapacity() {
      return capacity;
    }

    public void setCapacity(final int capacity) {
      this.capacity = capacity;
    }

    public DrainRate getDrainRate() {
      return drainRate;
    }

    public void setDrainRate(@NotNull final DrainRate drainRate) {
      this.drainRate = checkNotNull(drainRate);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("capacity", capacity)
          .add("drainRate", drainRate)
          .toString();
    }
  }

  /**
   * Bucket name for anonymous/guest users.
   */
  public static final String ANONYMOUS = "anonymous";

  /**
   * Bucket name for authenticated users.
   */
  public static final String AUTHENTICATED = "authenticated";

  /**
   * Maps request pattern to map of role-based bucket configurations.
   */
  public static class RequestStrategy
  {
    @NotNull
    @JsonProperty
    private Pattern pattern;

    @NotNull
    @Valid
    @JsonProperty
    private LinkedHashMap<String,Bucket> buckets = new LinkedHashMap<>();

    public Pattern getPattern() {
      return pattern;
    }

    public void setPattern(final Pattern pattern) {
      this.pattern = checkNotNull(pattern);
    }

    @NotNull
    public LinkedHashMap<String, Bucket> getBuckets() {
      return buckets;
    }

    public void setBuckets(@NotNull final LinkedHashMap<String, Bucket> buckets) {
      this.buckets = checkNotNull(buckets);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("pattern", pattern)
          .add("buckets", buckets)
          .toString();
    }
  }

  @NotNull
  @Valid
  @JsonProperty
  private List<RequestStrategy> requestStrategies = new ArrayList<>();

  public List<RequestStrategy> getRequestStrategies() {
    return requestStrategies;
  }

  public void setRequestStrategies(@NotNull final List<RequestStrategy> requestStrategies) {
    this.requestStrategies = checkNotNull(requestStrategies);
  }

  /**
   * Period of no activity when tracker is considered idle and removed.
   */
  @NotNull
  @Valid
  @JsonProperty
  private Duration idlePeriod = Duration.minutes(5);

  public Duration getIdlePeriod() {
    return idlePeriod;
  }

  public void setIdlePeriod(@NotNull final Duration idlePeriod) {
    this.idlePeriod = checkNotNull(idlePeriod);
  }
}
