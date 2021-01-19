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
package org.sonatype.goodies.dropwizard.guice;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link com.google.inject.Module} support.
 *
 * Adds additional helpers to cope with Sisu binding semantics and
 * {@link org.sonatype.goodies.dropwizard.config.ComponentDiscovery}.
 *
 * @since ???
 */
public class ModuleSupport
    extends AbstractModule
{
  private final Map<Class<?>,Multibinder<?>> multibinders = new HashMap<>();

  @SuppressWarnings({"rawtypes", "unchecked"})
  private <T> Multibinder<T> multibinder(final Class<T> type) {
    checkNotNull(type);
    Multibinder multibinder = multibinders.get(type);
    if (multibinder == null) {
      multibinder = Multibinder.newSetBinder(binder(), type);
      multibinders.put(type, multibinder);
    }
    return (Multibinder<T>)multibinder;
  }

  protected <T> void multibind(final Class<T> type, final Class<? extends T> target) {
    checkNotNull(target);
    multibinder(type).addBinding().to(target);
  }

  // TODO: consider helpers or not...

  ///**
  // * Binds given type as {@link Managed}.
  // */
  //protected void managed(final Class<? extends Managed> type) {
  //  multibind(Managed.class, type);
  //}
  //
  ///**
  // * Binds given type as {@link Task}.
  // */
  //protected void task(final Class<? extends Task> type) {
  //  multibind(Task.class, type);
  //}
  //
  ///**
  // * Binds given type as {@link HealthCheck}.
  // */
  //protected void healthCheck(final Class<? extends HealthCheck> type) {
  //  multibind(HealthCheck.class, type);
  //}
  //
  ///**
  // * Binds given type as {@link Component}.
  // */
  //protected void component(final Class<? extends Component> type) {
  //  multibind(Component.class, type);
  //}
  //
  ///**
  // * Binds given type as {@link Resource}.
  // */
  //protected void resource(final Class<? extends Resource> type) {
  //  multibind(Resource.class, type);
  //}
  //
  ///**
  // * Binds given type as {@link ExceptionMapper}.
  // */
  //protected void exceptionMapper(final Class<? extends ExceptionMapper> type) {
  //  multibind(ExceptionMapper.class, type);
  //}
  //
  ///**
  // * Binds given type as {@link Feature}.
  // */
  //protected void feature(final Class<? extends Feature> type) {
  //  multibind(Feature.class, type);
  //}
  //
  ///**
  // * Binds given type as {@link DynamicFeature}.
  // */
  //protected void dynamicFeature(final Class<? extends DynamicFeature> type) {
  //  multibind(DynamicFeature.class, type);
  //}
  //
  ///**
  // * Binds given type as {@link EnvironmentCustomizer}.
  // */
  //protected void environmentCustomizer(final Class<? extends EnvironmentCustomizer> type) {
  //  multibind(EnvironmentCustomizer.class, type);
  //}
}
