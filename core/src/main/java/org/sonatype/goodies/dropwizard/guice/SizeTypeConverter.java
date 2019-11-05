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

import javax.inject.Named;

import com.google.inject.spi.TypeConverter;
import io.dropwizard.util.Size;

/**
 * {@link Size} {@link TypeConverter}.
 *
 * @since ???
 */
@Named
public class SizeTypeConverter
    extends TypeConverterSupport<Size>
{
  @Override
  protected Size parse(final String value) {
    return Size.parse(value);
  }
}
