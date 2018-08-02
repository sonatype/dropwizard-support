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
package org.sonatype.goodies.dropwizard.swagger.internal;

import javax.inject.Named;

import org.sonatype.goodies.dropwizard.jaxrs.Resource;
import org.sonatype.goodies.dropwizard.swagger.SwaggerModel;

import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Resource} mediator.
 *
 * @since 1.0.0
 */
@Named
public class ResourceMediator
    implements Mediator<Named, Resource, SwaggerModel>
{
  private static final Logger log = LoggerFactory.getLogger(ResourceMediator.class);

  @Override
  public void add(final BeanEntry<Named, Resource> entry, final SwaggerModel swagger) {
    Class<Resource> type = entry.getImplementationClass();
    log.debug("Scan: {}", type);
    swagger.scan(type);
  }

  @Override
  public void remove(final BeanEntry<Named, Resource> entry, final SwaggerModel swagger) {
    // empty
  }
}