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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectCallable;
import org.apache.shiro.subject.support.SubjectRunnable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * System {@link Subject}.
 *
 * @since ???
 */
public class SystemSubject
    implements Subject
{
  public static final String PRINCIPAL = "*SYSTEM";

  private final PrincipalCollection principals;

  private SystemSubject() {
    this.principals = new SimplePrincipalCollection(PRINCIPAL, getClass().getName());
  }

  @Override
  public Object getPrincipal() {
    return PRINCIPAL;
  }

  @Override
  public PrincipalCollection getPrincipals() {
    return principals;
  }

  @Override
  public boolean isPermitted(final String permission) {
    return true;
  }

  @Override
  public boolean isPermitted(final Permission permission) {
    return true;
  }

  @Override
  public boolean[] isPermitted(final String... permissions) {
    return repeat(true, permissions.length);
  }

  @Override
  public boolean[] isPermitted(final List<Permission> permissions) {
    return repeat(true, permissions.size());
  }

  @Override
  public boolean isPermittedAll(final String... permissions) {
    return true;
  }

  @Override
  public boolean isPermittedAll(final Collection<Permission> permissions) {
    return true;
  }

  @Override
  public void checkPermission(final String permission) throws AuthorizationException {
    // empty
  }

  @Override
  public void checkPermission(final Permission permission) throws AuthorizationException {
    // empty
  }

  @Override
  public void checkPermissions(final String... permissions) throws AuthorizationException {
    // empty
  }

  @Override
  public void checkPermissions(final Collection<Permission> permissions) throws AuthorizationException {
    // empty
  }

  @Override
  public boolean hasRole(final String roleIdentifier) {
    return true;
  }

  @Override
  public boolean[] hasRoles(final List<String> roleIdentifiers) {
    return repeat(true, roleIdentifiers.size());
  }

  @Override
  public boolean hasAllRoles(final Collection<String> roleIdentifiers) {
    return true;
  }

  @Override
  public void checkRole(final String roleIdentifier) throws AuthorizationException {
    // empty
  }

  @Override
  public void checkRoles(final Collection<String> roleIdentifiers) throws AuthorizationException {
    // empty
  }

  @Override
  public void checkRoles(final String... roleIdentifiers) throws AuthorizationException {
    // empty
  }

  @Override
  public void login(final AuthenticationToken token) throws AuthenticationException {
    throw new RuntimeException("unsupported");
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public boolean isRemembered() {
    return false;
  }

  @Override
  public Session getSession() {
    throw new RuntimeException("unsupported");
  }

  @Override
  public Session getSession(final boolean create) {
    throw new RuntimeException("unsupported");
  }

  @Override
  public void logout() {
    throw new RuntimeException("unsupported");
  }

  @Override
  public <V> V execute(final Callable<V> callable) throws ExecutionException {
    try {
      return associateWith(callable).call();
    }
    catch (Throwable t) {
      throw new ExecutionException(t);
    }
  }

  @Override
  public void execute(final Runnable runnable) {
    associateWith(runnable).run();
  }

  @Override
  public <V> Callable<V> associateWith(final Callable<V> callable) {
    return new SubjectCallable<>(this, callable);
  }

  @Override
  public Runnable associateWith(final Runnable runnable) {
    return new SubjectRunnable(this, runnable);
  }

  @Override
  public void runAs(final PrincipalCollection principals) throws NullPointerException, IllegalStateException {
    throw new RuntimeException("unsupported");
  }

  @Override
  public boolean isRunAs() {
    return false;
  }

  @Override
  public PrincipalCollection getPreviousPrincipals() {
    return null;
  }

  @Override
  public PrincipalCollection releaseRunAs() {
    return null;
  }

  private static boolean[] repeat(final boolean flag, final int count) {
    checkArgument(count > -1);
    boolean[] result = new boolean[count];
    Arrays.fill(result, flag);
    return result;
  }

  private static final SystemSubject instance = new SystemSubject();

  public static SystemSubject get() {
    return instance;
  }
}
