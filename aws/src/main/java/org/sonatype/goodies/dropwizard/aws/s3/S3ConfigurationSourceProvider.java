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
package org.sonatype.goodies.dropwizard.aws.s3;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import com.amazonaws.services.s3.model.S3Object;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provide configuration from {@link S3Location}.
 *
 * @since ???
 */
public class S3ConfigurationSourceProvider
    implements ConfigurationSourceProvider
{
  private static final Logger log = LoggerFactory.getLogger(S3ConfigurationSourceProvider.class);

  @Nullable
  private S3Helper s3Helper;

  public S3ConfigurationSourceProvider(final @Nullable S3Helper s3Helper) {
    this.s3Helper = s3Helper;
  }

  public S3ConfigurationSourceProvider() {
    this(null);
  }

  /**
   * @see S3Location#parse(String)
   */
  @Override
  public InputStream open(final String path) throws IOException {
    checkNotNull(path);

    S3Location location = S3Location.parse(path);
    log.info("S3 configuration: {}", location);

    if (s3Helper == null) {
      s3Helper = new S3Helper();
    }

    S3Object object = s3Helper.get(location);
    return object.getObjectContent();
  }
}
