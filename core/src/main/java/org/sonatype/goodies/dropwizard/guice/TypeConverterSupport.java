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

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;
import org.eclipse.sisu.wire.AbstractTypeConverter;

/**
 * {@link TypeConverter} support.
 *
 * @since 1.3.0
 */
public abstract class TypeConverterSupport<T>
    extends AbstractTypeConverter<T>
{
  @Override
  public Object convert(final String value, final TypeLiteral<?> toType) {
    return parse(value);
  }

  protected abstract T parse(final String value);
}
