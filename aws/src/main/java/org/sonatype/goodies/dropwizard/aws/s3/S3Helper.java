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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.goodies.dropwizard.util.FileHelper;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * AWS Simple Storage Service (S3) helper.
 *
 * @since ???
 */
@Named
public class S3Helper
{
  private static final Logger log = LoggerFactory.getLogger(S3Helper.class);

  private final AmazonS3 client;

  @Inject
  public S3Helper(final AmazonS3 client) {
    this.client = checkNotNull(client);
  }

  public S3Helper() {
    this(AmazonS3ClientBuilder.defaultClient());
  }

  public AmazonS3 getClient() {
    return client;
  }

  public S3Object get(final String bucket, final String key) {
    checkNotNull(bucket);
    checkNotNull(key);

    log.info("GET {}/{}", bucket, key);

    return client.getObject(bucket, key);
  }

  public PutObjectResult put(final String bucket, final String key, final File file) {
    checkNotNull(bucket);
    checkNotNull(key);
    checkNotNull(file);
    checkArgument(file.exists());

    log.info("PUT {} -> {}/{}", file, bucket, key);

    PutObjectRequest request = new PutObjectRequest(bucket, key, file);
    return client.putObject(request);
  }

  public CopyObjectResult copy(final String sourceBucket,
                               final String sourceKey,
                               final String targetBucket,
                               final String targetKey)
  {
    checkNotNull(sourceBucket);
    checkNotNull(sourceKey);
    checkNotNull(targetBucket);
    checkNotNull(targetKey);

    log.info("COPY {}/{} -> {}/{}", sourceBucket, sourceKey, targetBucket, targetKey);

    CopyObjectRequest request = new CopyObjectRequest(sourceBucket, sourceKey, targetBucket, targetKey);
    return client.copyObject(request);
  }

  public void copyLocal(final S3Object source, final File target) throws IOException {
    checkNotNull(source);
    checkNotNull(target);

    log.info("CP {} -> {}", source, target);

    try (InputStream in = source.getObjectContent();
         OutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {
      IOUtils.copy(in, out);
    }
  }

  public File copyTemp(final S3Object source, final String prefix, final String suffix) throws IOException {
    checkNotNull(source);
    checkNotNull(prefix);
    checkNotNull(suffix);

    File file = null;
    try {
      file = File.createTempFile(prefix, suffix);
      file.deleteOnExit();
      copyLocal(source, file);
      return file;
    }
    catch (Exception e) {
      // if we failed to make a copy, ensure the file is removed
      if (file != null) {
        FileHelper.delete(file);
      }
      throw e;
    }
  }
}
