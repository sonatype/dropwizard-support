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
package org.sonatype.goodies.dropwizard.security.authz;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.Beta;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

// copied from: https://github.com/sonatype/nexus-public/blob/master/components/nexus-security/src/main/java/org/sonatype/nexus/security/SecurityHelper.java

/**
 * Authorization helper.
 *
 * @since 1.3.0
 */
@Named
@Singleton
@Beta
public class AuthorizationHelper
{
  private static final Logger log = LoggerFactory.getLogger(AuthorizationHelper.class);

  private Subject subject() {
    return SecurityUtils.getSubject();
  }

  /**
   * Ensure subject has given permissions.
   */
  public void ensurePermitted(final Subject subject, final Permission... permissions) {
    checkNotNull(subject);
    checkNotNull(permissions);
    checkArgument(permissions.length != 0);

    if (log.isTraceEnabled()) {
      log.trace("Ensuring subject '{}' has permissions: {}",
          subject.getPrincipal(), Arrays.toString(permissions));
    }
    subject.checkPermissions(Arrays.asList(permissions));
  }

  /**
   * Ensure subject has any of the given permissions.
   */
  public void ensureAnyPermitted(final Subject subject, final Permission... permissions) {
    checkNotNull(subject);
    checkNotNull(permissions);
    checkArgument(permissions.length != 0);

    if (log.isTraceEnabled()) {
      log.trace("Ensuring subject '{}' has any of the following permissions: {}",
          subject.getPrincipal(), Arrays.toString(permissions));
    }

    if (!anyPermitted(subject, permissions)) {
      throw new AuthorizationException("User is not permitted.");
    }
  }

  /**
   * Ensure current subject has given permissions.
   */
  public void ensurePermitted(final Permission... permissions) {
    ensurePermitted(subject(), permissions);
  }

  /**
   * Check if subject has ANY of the given permissions.
   */
  public boolean anyPermitted(final Subject subject, final Permission... permissions) {
    checkNotNull(subject);
    checkNotNull(permissions);
    checkArgument(permissions.length != 0);

    boolean trace = log.isTraceEnabled();
    if (trace) {
      log.trace("Checking if subject '{}' has ANY of these permissions: {}",
          subject.getPrincipal(), Arrays.toString(permissions));
    }
    for (Permission permission : permissions) {
      if (subject.isPermitted(permission)) {
        if (trace) {
          log.trace("Subject '{}' has permission: {}", subject.getPrincipal(), permission);
        }
        return true;
      }
    }
    if (trace) {
      log.trace("Subject '{}' missing required permissions: {}",
          subject.getPrincipal(), Arrays.toString(permissions));
    }
    return false;
  }

  /**
   * Check if subject has ANY of the given permissions.
   */
  public boolean anyPermitted(final Subject subject, final Iterable<Permission> permissions) {
    return anyPermitted(
        subject,
        StreamSupport.stream(permissions.spliterator(), false).toArray(Permission[]::new)
    );
  }

  /**
   * Check if current subject has ANY of the given permissions.
   */
  public boolean anyPermitted(final Permission... permissions) {
    return anyPermitted(subject(), permissions);
  }

  /**
   * Check if subject has ALL of the given permissions.
   */
  public boolean allPermitted(final Subject subject, final Permission... permissions) {
    checkNotNull(subject);
    checkNotNull(permissions);
    checkArgument(permissions.length != 0);

    boolean trace = log.isTraceEnabled();
    if (trace) {
      log.trace("Checking if subject '{}' has ALL of these permissions: {}",
          subject.getPrincipal(), Arrays.toString(permissions));
    }
    for (Permission permission : permissions) {
      if (!subject.isPermitted(permission)) {
        if (trace) {
          log.trace("Subject '{}' missing permission: {}", subject.getPrincipal(), permission);
        }
        return false;
      }
    }

    if (trace) {
      log.trace("Subject '{}' has required permissions: {}",
          subject.getPrincipal(), Arrays.toString(permissions));
    }
    return true;
  }

  /**
   * Check if current subject has ALL of the given permissions.
   */
  public boolean allPermitted(final Permission... permissions) {
    return allPermitted(subject(), permissions);
  }

  /**
   * Check which permissions the subject has.
   */
  public boolean[] isPermitted(final Subject subject, final Permission... permissions) {
    checkNotNull(subject);
    checkNotNull(permissions);
    checkArgument(permissions.length != 0);

    boolean trace = log.isTraceEnabled();
    if (trace) {
      log.trace("Checking which permissions subject '{}' has in: {}", subject.getPrincipal(),
          Arrays.toString(permissions));
    }
    boolean[] results = subject.isPermitted(Arrays.asList(permissions));
    if (trace) {
      log.trace("Subject '{}' has permissions: [{}] results {}",
          subject.getPrincipal(), Arrays.toString(permissions), results);
    }
    return results;
  }

  /**
   * Check which permissions the current subject has.
   */
  public boolean[] isPermitted(final Permission... permissions) {
    return isPermitted(subject(), permissions);
  }
}
