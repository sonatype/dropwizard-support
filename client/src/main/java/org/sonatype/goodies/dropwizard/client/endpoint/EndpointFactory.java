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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
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
 * @since 1.0.0
 * @see EndpointException
 * @see EndpointErrorException
 */
public final class EndpointFactory
{
  private static final Logger log = LoggerFactory.getLogger(EndpointFactory.class);

  private static final MultivaluedMap<String, Object> EMPTY_HEADERS = new MultivaluedHashMap<>();

  private static final List<Cookie> EMPTY_COOKIES = Collections.emptyList();

  private static final Form EMPTY_FORM = new Form();

  private EndpointFactory() {
    // empty
  }

  /**
   * @since 1.0.2
   */
  @SuppressWarnings("unchecked")
  public static <T> T create(final Class<T> intf,
                             final WebTarget target,
                             @Nullable final MultivaluedMap<String,Object> headers,
                             @Nullable final List<Cookie> cookies,
                             @Nullable final Form form)
  {
    checkNotNull(intf);
    checkNotNull(target);

    T result = (T) Proxy.newProxyInstance(
        AccessController.doPrivileged(ReflectionHelper.getClassLoaderPA(intf)),
        new Class[]{intf},
        handler(
            intf,
            target,
            headers == null ? EMPTY_HEADERS : headers,
            cookies == null ? EMPTY_COOKIES : cookies,
            form == null ? EMPTY_FORM : form
        )
    );
    log.trace("Endpoint: {}", result);

    return result;
  }

  public static <T> T create(final Class<T> intf, final WebTarget target) {
    return create(intf, target, null, null, null);
  }

  /**
   * @since ??
   */
  public static <T> Builder<T> builder(final Class<T> intf, final WebTarget target) {
    return new Builder<>(intf, target);
  }

  //
  // Builder
  //

  /**
   * @since 1.0.2
   */
  public static class Builder<T>
  {
    private final Class<T> intf;

    private final WebTarget target;

    private MultivaluedMap<String,Object> headers;

    private List<Cookie> cookies;

    private Form form;

    private Builder(final Class<T> intf, final WebTarget target) {
      this.intf = checkNotNull(intf);
      this.target = checkNotNull(target);
    }

    public Builder<T> headers(@Nullable final MultivaluedMap<String,Object> headers) {
      this.headers = headers;
      return this;
    }

    public Builder<T> header(final String name, final Object value) {
      checkNotNull(name);
      checkNotNull(value);
      if (headers == null) {
        headers = new MultivaluedHashMap<>();
      }
      headers.putSingle(name, value);
      return this;
    }

    public Builder<T> cookies(@Nullable final List<Cookie> cookies) {
      this.cookies = cookies;
      return this;
    }

    public Builder<T> cookie(final Cookie cookie) {
      checkNotNull(cookie);
      if (cookies == null) {
        cookies = new ArrayList<>();
      }
      cookies.add(cookie);
      return this;
    }

    public Builder<T> form(@Nullable final Form form) {
      this.form = form;
      return this;
    }

    public T build() {
      return create(intf, target, headers, cookies, form);
    }
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

    private Handler(final InvocationHandler delegate) {
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

  private static Constructor<WebResourceFactory> factory;

  // NOTE: some reflection here to access private WebResourceFactory ctor, and avoid needless proxy of proxy to impl

  private static Handler handler(final Class intf,
                                 final WebTarget target,
                                 final MultivaluedMap<String,Object> headers,
                                 final List<Cookie> cookies,
                                 final Form form)
  {
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
      delegate = factory.newInstance(resolved, headers, cookies, form);
      log.trace("Delegate: {}", delegate);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    // wrap with customized handler
    return new Handler(delegate);
  }
}
