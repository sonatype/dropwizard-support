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
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import org.sonatype.goodies.dropwizard.ratelimit.RatelimitConfiguration.Bucket;
import org.sonatype.goodies.dropwizard.ratelimit.RatelimitConfiguration.DrainRate;
import org.sonatype.goodies.dropwizard.ratelimit.RatelimitConfiguration.RequestStrategy;
import org.sonatype.goodies.dropwizard.ratelimit.RatelimitTracker.Identifier;
import org.sonatype.goodies.dropwizard.security.authz.RoleMatchingHelper;
import org.sonatype.goodies.dropwizard.security.subject.SubjectHelper;
import org.sonatype.goodies.dropwizard.service.ServiceSupport;
import org.sonatype.goodies.dropwizard.util.IpAddresses;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Ticker;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.dropwizard.util.Duration;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.sonatype.goodies.dropwizard.ratelimit.RatelimitConfiguration.ANONYMOUS;
import static org.sonatype.goodies.dropwizard.ratelimit.RatelimitConfiguration.AUTHENTICATED;

/**
 * Default {@link RatelimitService}.
 *
 * @since 1.2.0
 */
@Named
@Singleton
public class RatelimitServiceImpl
    extends ServiceSupport
    implements RatelimitService
{
  private final RatelimitConfiguration config;

  /**
   * List of IP addresses to whitelist.
   *
   * Whitelisted IP by-passes rate-limiting strategies.
   */
  private final IpAddresses whitelist = new IpAddresses();

  /**
   * Configured request-strategies to rate-limit requests based on pattern matching.
   */
  private final List<RequestStrategy> requestStrategies = new ArrayList<>();

  /**
   * Mapping of tracker identifier to tracker.
   */
  private final ConcurrentHashMap<Identifier, RatelimitTracker> trackers = new ConcurrentHashMap<>();

  private final Ticker ticker = Ticker.systemTicker();

  private final Meter rejected;

  private ScheduledThreadPoolExecutor scheduler;

  @Inject
  public RatelimitServiceImpl(final RatelimitConfiguration config, final MetricRegistry metricRegistry) {
    this.config = checkNotNull(config);
    this.rejected = metricRegistry.meter("service.ratelimit.rejected");
    metricRegistry.register("service.ratelimit.trackers.count", (Gauge<Integer>) trackers::size);
  }

  @Override
  protected void doStart() throws Exception {
    whitelist.setAddresses(config.getWhitelist());

    requestStrategies.addAll(config.getRequestStrategies());
    log.info("Request strategies:");
    for (RequestStrategy requestStrategy : requestStrategies) {
      log.info("  {} ->", requestStrategy.getPattern());
      LinkedHashMap<String, Bucket> buckets = requestStrategy.getBuckets();
      for (Entry<String,Bucket> entry : buckets.entrySet()) {
        log.info("    {}={}", entry.getKey(), entry.getValue());
      }

      checkState(buckets.get(ANONYMOUS) != null, "Missing bucket: %s", ANONYMOUS);
      checkState(buckets.get(AUTHENTICATED) != null, "Missing bucket: %s", AUTHENTICATED);
    }

    ThreadFactory factory = new ThreadFactoryBuilder()
        .setNameFormat("ratelimit-reaper-%d")
        .build();
    scheduler = new ScheduledThreadPoolExecutor(1, factory);
    scheduler.setRemoveOnCancelPolicy(true);
  }

  @Override
  protected void doStop() throws Exception {
    if (scheduler != null) {
      scheduler.shutdownNow();
      scheduler = null;
    }

    trackers.clear();
    requestStrategies.clear();
    whitelist.clear();
  }

  //
  // Whitelist
  //

  @Override
  public IpAddresses getWhitelist() {
    return whitelist;
  }

  @Override
  public boolean isWhitelisted(final String address) {
    checkNotNull(address);
    log.trace("Testing address for whitelist: {}", address);
    return whitelist.match(address);
  }

  //
  // Request strategy
  //

  @Nullable
  @Override
  public RequestStrategy strategy(final HttpServletRequest request) {
    for (RequestStrategy strategy : requestStrategies) {
      Pattern pattern = strategy.getPattern();
      String path = request.getRequestURI();

      log.trace("Checking path: {} -> {}", pattern, path);
      if (pattern.matcher(path).matches()) {
        return strategy;
      }
    }
    return null;
  }

  //
  // Trackers
  //

  /**
   * Attribute {@link RatelimitTracker} is attached to request-context.
   */
  private static final String TRACKER_ATTR = RatelimitTracker.class.toString();

  /**
   * Identify request.
   */
  @VisibleForTesting
  Identifier identify(final HttpServletRequest request) {
    String username = SubjectHelper.getUsername();
    if (username != null) {
      return new Identifier(Identifier.Type.USERNAME, username);
    }
    else {
      return new Identifier(Identifier.Type.REMOTE_IP, request.getRemoteAddr());
    }
  }

  @Override
  public RatelimitTracker tracker(final HttpServletRequest request) {
    RatelimitTracker tracker = (RatelimitTracker) request.getAttribute(TRACKER_ATTR);

    // if there is no tracker in request-context then detect
    if (tracker == null) {
      Identifier id = identify(request);
      log.trace("Identifier: {}", id);

      tracker = trackers.computeIfAbsent(id, (Function<Identifier, RatelimitTracker>) input -> {
        RatelimitTracker result = new RatelimitTracker(input);
        log.trace("Created tracker: {}", result);

        // schedule idle expiration reaper
        schedule(new IdleTrackerReaper(result));
        return result;
      });

      // attach tracker to request
      request.setAttribute(TRACKER_ATTR, tracker);
    }
    return tracker;
  }

  /**
   * Scheduled task to reap idle trackers.
   */
  private class IdleTrackerReaper
      implements Runnable
  {
    private final RatelimitTracker tracker;

    private final long idlePeriodNanos = config.getIdlePeriod().toNanoseconds();

    public IdleTrackerReaper(final RatelimitTracker tracker) {
      this.tracker = tracker;
    }

    @Override
    public void run() {
      long now = ticker.read();
      long touched = tracker.getTimestamp();
      long delta = now - touched;

      // tracker is expired, never touched (ie. 0) or delta from last touch and now exceeds idle-period
      boolean expired = touched == 0 || delta >= idlePeriodNanos;

      if (expired) {
        trackers.remove(tracker.getId());
        if (log.isDebugEnabled()) {
          log.trace("Removed idle ({} seconds) tracker: {}", Duration.nanoseconds(delta).toSeconds(), tracker);
        }
      }
      else {
        // else re-schedule to expire again
        schedule(this);
      }
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("tracker", tracker)
          .toString();
    }
  }

  private void schedule(final IdleTrackerReaper reaper) {
    Duration period = config.getIdlePeriod();
    scheduler.schedule(reaper, period.getQuantity(), period.getUnit());
    log.trace("Scheduled tracker reaper: {} -> {}", reaper, period);
  }

  /**
   * Resolve the bucket configuration for current user and strategy.
   *
   * This is called only when a new bucket is created.  No need to consider caching.
   */
  private Bucket bucketConfiguration(final RequestStrategy strategy) {
    LinkedHashMap<String,Bucket> buckets = strategy.getBuckets();
    Subject subject = SecurityUtils.getSubject();

    String selected;
    if (SubjectHelper.isAnonymous(subject)) {
      selected = ANONYMOUS;
    }
    else {
      selected = RoleMatchingHelper.matchFirst(subject, buckets.keySet());
      if (selected == null) {
        selected = AUTHENTICATED;
      }
    }

    Bucket config = buckets.get(selected);
    log.debug("Selected bucket configuration: {} -> {}", selected, config);
    return config;
  }

  /**
   * Resolve the bucket or create.
   */
  private LeakyBucket bucket(final RatelimitTracker tracker, final RequestStrategy strategy) {
    // resolve the bucket, or create
    return tracker.getBuckets().computeIfAbsent(strategy, (Function<RequestStrategy, LeakyBucket>) input -> {
      // resolve bucket configuration for current user
      Bucket config = bucketConfiguration(input);
      DrainRate drainRate = config.getDrainRate();
      LeakyBucket bucket = new LeakyBucket(ticker, config.getCapacity(), drainRate.getCount(), drainRate.getPeriod());
      log.trace("Created bucket: {}", bucket);

      return bucket;
    });
  }

  @Override
  public boolean tick(final RatelimitTracker tracker, final RequestStrategy strategy) {
    checkNotNull(tracker);
    checkNotNull(strategy);

    log.trace("Tick; tracker: {}, strategy: {}", tracker, strategy);
    tracker.touch(ticker);

    LeakyBucket bucket = bucket(tracker, strategy);
    log.trace("Bucket: {}", bucket);

    // fill one drop
    boolean overflown = bucket.fill();

    if (overflown) {
      rejected.mark();
    }

    return overflown;
  }
}
