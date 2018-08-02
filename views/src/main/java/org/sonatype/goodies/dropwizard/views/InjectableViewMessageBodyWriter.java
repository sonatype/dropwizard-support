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
package org.sonatype.goodies.dropwizard.views;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.views.View;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.ViewRenderer;
import org.glassfish.hk2.api.ServiceLocator;

/**
 * Injectable view {@link javax.ws.rs.ext.MessageBodyWriter}.
 *
 * @see InjectableViewBundle
 * @since 1.0.0
 */
@Provider
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML})
public class InjectableViewMessageBodyWriter
    extends ViewMessageBodyWriter
{
  @Inject
  private ServiceLocator serviceLocator;

  public InjectableViewMessageBodyWriter(final MetricRegistry metrics, final Iterable<ViewRenderer> renderers) {
    super(metrics, renderers);
  }

  @Override
  public void writeTo(final View view,
                      final Class<?> type,
                      final Type genericType,
                      final Annotation[] annotations,
                      final MediaType mediaType,
                      final MultivaluedMap<String, Object> httpHeaders,
                      final OutputStream entityStream) throws IOException
  {
    // request view injection
    serviceLocator.inject(view);

    // and then render per-default implementation
    super.writeTo(view, type, genericType, annotations, mediaType, httpHeaders, entityStream);
  }
}
