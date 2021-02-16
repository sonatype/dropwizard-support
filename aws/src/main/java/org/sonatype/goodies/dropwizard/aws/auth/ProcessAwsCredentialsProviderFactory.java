/*
 * Copyright (c) 2020-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.auth;

import javax.validation.constraints.NotEmpty;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ProcessCredentialsProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;
import io.dropwizard.util.Duration;

/**
 * {@link ProcessCredentialsProvider} factory.
 *
 * @since ???
 */
@JsonTypeName("process")
public class ProcessAwsCredentialsProviderFactory
  implements AwsCredentialsProviderFactory
{
  @NotEmpty
  @JsonProperty
  private String command;

  public String getCommand() {
    return command;
  }

  public void setCommand(final String command) {
    this.command = command;
  }

  @JsonProperty
  private Duration expirationBuffer = Duration.seconds(15);

  public Duration getExpirationBuffer() {
    return expirationBuffer;
  }

  public void setExpirationBuffer(final Duration expirationBuffer) {
    this.expirationBuffer = expirationBuffer;
  }

  @JsonProperty
  private long processOutputLimit = 64000;

  public long getProcessOutputLimit() {
    return processOutputLimit;
  }

  public void setProcessOutputLimit(final long processOutputLimit) {
    this.processOutputLimit = processOutputLimit;
  }

  @Override
  public AWSCredentialsProvider create() {
    return ProcessCredentialsProvider.builder()
        .withCommand(command)
        .withCredentialExpirationBuffer(Ints.checkedCast(expirationBuffer.getQuantity()), expirationBuffer.getUnit())
        .withProcessOutputLimit(processOutputLimit)
        .build();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("command", command)
        .add("expirationBuffer", expirationBuffer)
        .add("processOutputLimit", processOutputLimit)
        .toString();
  }
}
