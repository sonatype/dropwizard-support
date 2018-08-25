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
package org.sonatype.goodies.dropwizard.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import org.sonatype.goodies.dropwizard.service.ServiceSupport;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link RequestRuleService}.
 *
 * @since ???
 */
@Named
@Singleton
public class RequestRuleServiceImpl
    extends ServiceSupport
    implements RequestRuleService
{
  private final RequestRuleConfiguration config;

  @Nullable
  private final Injector injector;

  @Nullable
  private RequestRule[] rules;

  @Inject
  public RequestRuleServiceImpl(final RequestRuleConfiguration config,
                                @Nullable final Injector injector)
  {
    this.config = checkNotNull(config);
    this.injector = injector;
  }

  @Override
  protected void doStart() throws Exception {
    List<RequestRule> configured = config.getRules();
    if (configured.isEmpty()) {
      log.debug("No rules");
    }
    else {
      int count = configured.size();
      log.debug("{} rules:", count);
      rules = new RequestRule[count];
      for (int i=0; i<count; i++) {
        RequestRule rule = configured.get(i);

        // if injection is enabled, then inject rule
        if (injector != null) {
          injector.injectMembers(rule);
        }

        log.info("Rule[{}]: {}", i, rule);
        rules[i] = rule;
      }
    }
  }

  @Override
  protected void doStop() throws Exception {
    rules = null;
  }

  @Override
  public List<RequestRule> getRules() {
    ensureStarted();

    if (rules == null) {
      return Collections.emptyList();
    }

    return ImmutableList.copyOf(rules);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T extends RequestRule> T getRule(final Class<T> type) {
    ensureStarted();
    checkNotNull(type);

    if (rules != null) {
      for (RequestRule rule : rules) {
        if (type.isAssignableFrom(rule.getClass())) {
          return (T) rule;
        }
      }
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends RequestRule> List<T> getRules(final Class<T> type) {
    ensureStarted();
    checkNotNull(type);

    if (rules == null) {
      return Collections.emptyList();
    }

    List<T> result = new ArrayList<>();
    for (RequestRule rule : rules) {
      if (type.isAssignableFrom(rule.getClass())) {
        result.add((T) rule);
      }
    }

    return result;
  }

  @Nullable
  @Override
  public RequestRuleResult evaluate(final HttpServletRequest request) {
    checkNotNull(request);
    ensureStarted();

    if (rules != null && rules.length != 0) {
      final boolean trace = log.isTraceEnabled();

      if (trace) {
        log.trace("Evaluating: {}", request);
      }

      for (int i=0; i<rules.length; i++) {
        RequestRule rule = rules[i];
        if (trace) {
          log.trace("Rule[{}]: {}", i, rule);
        }
        RequestRuleResult result = rule.evaluate(request);

        if (result != null) {
          if (trace) {
            log.trace("Result: {}", result);
          }
          return result;
        }
      }
    }

    return null;
  }
}
