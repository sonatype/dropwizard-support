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
package org.sonatype.goodies.dropwizard.camel;

import java.io.File;

import org.sonatype.goodies.dropwizard.common.io.FileHelper;

import org.apache.camel.Exchange;
import org.apache.camel.spi.Synchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Delete file on {@link Exchange} completion.
 *
 * @since 1.3.0
 */
public class DeleteFileSynchronization
    implements Synchronization
{
  private static final Logger log = LoggerFactory.getLogger(DeleteFileSynchronization.class);

  private final File file;

  public DeleteFileSynchronization(final File file) {
    this.file = checkNotNull(file);
  }

  @Override
  public void onComplete(final Exchange exchange) {
    on(exchange);
  }

  @Override
  public void onFailure(final Exchange exchange) {
    on(exchange);
  }

  private void on(final Exchange exchange) {
    log.debug("Deleting: {}", file);
    FileHelper.delete(file);
  }
}
