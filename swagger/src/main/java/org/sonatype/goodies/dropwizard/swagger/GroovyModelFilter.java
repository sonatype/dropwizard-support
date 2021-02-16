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
package org.sonatype.goodies.dropwizard.swagger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Singleton;

import com.google.common.collect.ImmutableSet;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;

/**
 * Ban groovy meta-class properties.
 *
 * @since 1.0.0
 */
@Singleton
public class GroovyModelFilter
    implements ModelConverter
{
  private static final Set<String> BANNED_TYPE_NAMES = ImmutableSet.of(
      "[simple type, class groovy.lang.MetaClass]" // groovy's MetaClass typeName
  );

  @Override
  public Model resolve(final Type type,
                       final ModelConverterContext context,
                       final Iterator<ModelConverter> chain)
  {
    if (!BANNED_TYPE_NAMES.contains(type.getTypeName()) && chain.hasNext()) {
      return chain.next().resolve(type, context, chain);
    }
    return null;
  }

  @Override
  public Property resolveProperty(final Type type,
                                  final ModelConverterContext context,
                                  final Annotation[] annotations,
                                  final Iterator<ModelConverter> chain)
  {
    if (!BANNED_TYPE_NAMES.contains(type.getTypeName()) && chain.hasNext()) {
      return chain.next().resolveProperty(type, context, annotations, chain);
    }
    return null;
  }
}
