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
package org.sonatype.goodies.dropwizard.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import org.apache.shiro.authz.permission.WildcardPermission;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

// copied from https://github.com/sonatype/nexus-public/blob/master/components/nexus-security/src/main/java/org/sonatype/nexus/security/authz/WildcardPermission2.java

/**
 * {@link WildcardPermission} which caches {@link #hashCode} for improved performance.
 *
 * @since ???
 */
public class WildcardPermission2
    extends WildcardPermission
{
  private static final boolean CASE_SENSITIVE = true;

  private int cachedHash;

  protected WildcardPermission2() {
    // empty
  }

  public WildcardPermission2(final String wildcardString) {
    this(wildcardString, DEFAULT_CASE_SENSITIVE);
  }

  public WildcardPermission2(final String wildcardString, final boolean caseSensitive) {
    super(wildcardString, caseSensitive);
  }

  /**
   * Caches {@link #hashCode()} after parts are installed.
   */
  @Override
  protected void setParts(final String wildcardString, final boolean caseSensitive) {
    super.setParts(wildcardString, caseSensitive);
    this.cachedHash = super.hashCode();
  }

  protected void setParts(final List<String> subParts, final List<String> actions) {
    setParts(subParts, actions, !CASE_SENSITIVE);
  }

  protected void setParts(final List<String> subParts, final List<String> actions, final boolean caseSensitive) {
    List<Set<String>> parts = new ArrayList<>();
    subParts.forEach(subPart -> parts.add(toPart(subPart, caseSensitive)));
    parts.add(toPart(actions, caseSensitive));
    setParts(parts);
    this.cachedHash = super.hashCode();
  }

  @VisibleForTesting
  protected List<Set<String>> getParts() {
    return super.getParts();
  }

  private static Set<String> toPart(final String subpart, final boolean caseSensitive) {
    return ImmutableSet.of(caseSensitive ? subpart : subpart.toLowerCase());
  }

  private static Set<String> toPart(final List<String> actions, final boolean caseSensitive) {
    if (actions.size() == 1) {
      return toPart(actions.get(0), caseSensitive);
    }
    return actions.stream().map(action -> caseSensitive ? action : action.toLowerCase()).collect(toImmutableSet());
  }

  @Override
  public int hashCode() {
    return cachedHash;
  }

  private static final Joiner JOINER = Joiner.on(',');

  /**
   * Customized string representation to avoid {@code []} syntax from sets.
   */
  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder();
    for (Set<String> part : getParts()) {
      if (buff.length() > 0) {
        buff.append(':');
      }
      JOINER.appendTo(buff, part);
    }
    return buff.toString();
  }
}
