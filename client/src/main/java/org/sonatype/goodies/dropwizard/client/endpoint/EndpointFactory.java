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
package org.sonatype.goodies.dropwizard.client.endpoint;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Endpoint factory.
 *
 * Creates REST-client proxies based on {@link WebResourceFactory}, with additional exception handling and logging.
 *
 * @since ???
 * @see EndpointException
 * @see EndpointErrorException
 */
public final class EndpointFactory
{
  private static final Logger log = LoggerFactory.getLogger(EndpointFactory.class);

  private EndpointFactory() {
    // empty
  }

  @SuppressWarnings("unchecked")
  public static <T> T create(final Class<T> intf, final WebTarget target) {
    checkNotNull(intf);
    checkNotNull(target);

    T result = (T) Proxy.newProxyInstance(
        AccessController.doPrivileged(ReflectionHelper.getClassLoaderPA(intf)),
        new Class[]{intf},
        handler(intf, target)
    );
    log.trace("Endpoint: {}", result);

    return result;
  }

  //
  // Handler
  //

  /**
   * Custom handler which delegates to {@link WebResourceFactory} and provides exception handling.
   */
  private static class Handler
      implements InvocationHandler
  {
    private final InvocationHandler delegate;

    public Handler(final InvocationHandler delegate) {
      this.delegate = delegate;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
      try {
        return delegate.invoke(proxy, method, args);
      }
      catch (ClientErrorException e) {
        log.trace("Exception", e);
        throw new EndpointException(e);
      }
      catch (Exception e) {
        log.trace("Error", e);
        throw new EndpointErrorException(e);
      }
    }
  }

  // TODO: atm we only support the basic usage with default header, cookies, form and path from intf
  // TODO: if/when needed expose a builder to configure these bits

  private static final MultivaluedMap<String, Object> EMPTY_HEADERS = new MultivaluedHashMap<>();

  private static final List<Cookie> EMPTY_COOKIES = Collections.emptyList();

  private static final Form EMPTY_FORM = new Form();

  private static Constructor<WebResourceFactory> factory;

  // NOTE: some reflection here to access private WebResourceFactory ctor, and avoid needless proxy of proxy to impl

  private static Handler handler(final Class intf, final WebTarget target) {
    // resolve delegate factory
    if (factory == null) {
      try {
        factory = WebResourceFactory.class.getDeclaredConstructor(
            WebTarget.class,
            MultivaluedMap.class,
            List.class,
            Form.class
        );
        log.trace("Factory: {}", factory);
      }
      catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    // resolve target to @Path
    WebTarget resolved = target;
    Path path = ((AnnotatedElement)intf).getAnnotation(Path.class);
    if (path != null) {
      resolved = target.path(path.value());
    }

    // construct delegate
    InvocationHandler delegate;
    try {
      factory.setAccessible(true);
      delegate = factory.newInstance(resolved, EMPTY_HEADERS, EMPTY_COOKIES, EMPTY_FORM);
      log.trace("Delegate: {}", delegate);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    // wrap with customized handler
    return new Handler(delegate);
  }
}
