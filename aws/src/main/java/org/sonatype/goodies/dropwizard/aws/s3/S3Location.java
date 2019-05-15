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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectId;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * S3 location, which is URI-like but needs additional normalization.
 *
 * @since ???
 */
public class S3Location
{
  public static final String SCHEME = "s3";

  private final String bucket;

  private final String key;

  public S3Location(final String bucket, final String key) {
    this.bucket = checkNotNull(bucket);
    this.key = checkNotNull(key);
  }

  public String getBucket() {
    return bucket;
  }

  public String getKey() {
    return key;
  }

  public URI toUri() {
    try {
      return new URI(SCHEME, null, bucket, -1, "/" + key, null, null);
    }
    catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    S3Location that = (S3Location) o;
    return Objects.equals(bucket, that.bucket) &&
        Objects.equals(key, that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bucket, key);
  }

  @Override
  public String toString() {
    // TODO: ensure sane uri-encoding of bucket and key; see: https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingMetadata.html
    return String.format("%s://%s/%s", SCHEME, bucket, key);
  }

  //
  // Parsing
  //

  public static S3Location parse(final String value) {
    checkNotNull(value);

    // FIXME: normalize value, unsure why but the value given is not a properly formed URI
    String normalized = value.replaceAll(" ", "%20")
        .replaceAll("\\[", "%5B")
        .replaceAll("]", "%5D");

    URI uri = URI.create(normalized);
    return parse(uri);
  }

  public static S3Location parse(final URI value) {
    checkNotNull(value);

    if (!SCHEME.equals(value.getScheme())) {
      throw new RuntimeException("Invalid scheme for S3 location: " + value);
    }

    String bucket = value.getHost();
    // strip off leading / from object key
    String key = value.getPath().substring(1);

    return new S3Location(bucket, key);
  }

  //
  // Factory
  //

  public static S3Location create(final S3ObjectId objectId) {
    checkNotNull(objectId);
    return new S3Location(objectId.getBucket(), objectId.getKey());
  }

  public static S3Location create(final S3Object object) {
    checkNotNull(object);
    return new S3Location(object.getBucketName(), object.getKey());
  }

  public static S3Location create(final S3Entity entity) {
    checkNotNull(entity);
    S3BucketEntity bucket = checkNotNull(entity.getBucket());
    S3ObjectEntity object = checkNotNull(entity.getObject());
    return new S3Location(bucket.getName(), object.getUrlDecodedKey());
  }
}
