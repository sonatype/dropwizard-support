/*
 * Copyright (c) 2019-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.s3;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import com.amazonaws.services.s3.model.S3Object;

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

  public S3Location(final S3Object object) {
    checkNotNull(object);
    this.bucket = object.getBucketName();
    this.key = object.getKey();
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
    return String.format("%s://%s/%s", SCHEME, bucket, key);
  }

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
}
