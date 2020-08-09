/*
 * Copyright (c) 2019-present Sonatype, Inc. All rights reserved.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.goodies.dropwizard.aws.sqs;

import javax.inject.Provider;

import org.sonatype.goodies.dropwizard.health.HealthCheckSupport;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.codahale.metrics.health.HealthCheck;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * AWS SQS queue {@link HealthCheck}.
 *
 * @since ???
 */
public class SqsQueueExistsHealthCheck
    extends HealthCheckSupport
{
  private final Provider<AmazonSQS> sqsClientFactory;

  private final String queueName;

  public SqsQueueExistsHealthCheck(final Provider<AmazonSQS> sqsClientFactory, final String queueName) {
    this.sqsClientFactory = checkNotNull(sqsClientFactory);
    this.queueName = checkNotNull(queueName);
  }

  @Override
  protected Result check() throws Exception {
    AmazonSQS sqsClient = sqsClientFactory.get();
    try {
      GetQueueUrlResult result = sqsClient.getQueueUrl(new GetQueueUrlRequest().withQueueName(queueName));
      return Result.builder()
          .withDetail("queue-url", result.getQueueUrl())
          .healthy()
          .build();
    }
    catch (QueueDoesNotExistException e) {
      return Result.unhealthy("Missing queue: %s", queueName);
    }
    finally {
      sqsClient.shutdown();
    }
  }
}
