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
package org.sonatype.goodies.dropwizard.assets;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Resources;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.servlets.assets.AssetServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Custom {@link AssetsBundle} which exposes class-context resource loading.
 *
 * @since 1.0.0
 */
public class ClassContextAssetsBundle
    extends AssetsBundle
{
  private static final Logger log = LoggerFactory.getLogger(ClassContextAssetsBundle.class);

  private final Class<?> owner;

  public ClassContextAssetsBundle(final Class<?> owner,
                                  final String resourcePath,
                                  final String uriPath,
                                  final String indexFile,
                                  final String assetsName)
  {
    super(resourcePath, uriPath, indexFile, assetsName);
    this.owner = checkNotNull(owner);
  }

  @Override
  protected AssetServlet createServlet() {
    return new AssetServlet(getResourcePath(), getUriPath(), getIndexFile(), StandardCharsets.UTF_8)
    {
      @Override
      protected URL getResourceUrl(final String resourcePath) {
        log.trace("Find resource: {}", resourcePath);

        // HACK: for some reason super impl strips off leading / which prevents resolution from class-context
        URL resource = Resources.getResource(owner, "/" + resourcePath);
        log.trace("Found: {}", resource);

        return resource;
      }
    };
  }
}
