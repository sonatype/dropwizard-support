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
package org.sonatype.goodies.testsupport;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

// copied from: https://github.com/sonatype/goodies/blob/master/testsupport/src/main/java/org/sonatype/goodies/testsupport/TestTracer.java

/**
 * Traces test execution to a {@link Logger}.
 *
 * @since 1.2.0
 * @deprecated Provided for compatibility only.
 */
@Deprecated
public class TestTracer
    extends TestWatcher
{
  private static final String UNKNOWN_METHOD_NAME = "UNKNOWN METHOD NAME";

  private final Logger logger;

  public TestTracer(final Logger logger) {
    this.logger = requireNonNull(logger);
  }

  public TestTracer(final Object owner) {
    this(LoggerFactory.getLogger(owner.getClass()));
  }

  protected String prefix(final Description desc) {
    return format("TEST %s", desc == null ? UNKNOWN_METHOD_NAME : desc.getMethodName());
  }

  protected void log(String message, Object... args) {
    logger.info(message, args);
  }

  @Override
  public void starting(final Description desc) {
    log("{} STARTING", prefix(desc));
  }

  @Override
  public void succeeded(final Description desc) {
    log("{} SUCCEEDED", prefix(desc));
  }

  @Override
  public void failed(final Throwable e, final Description desc) {
    if (e instanceof MultipleFailureException) {
      MultipleFailureException mfe = (MultipleFailureException) e;
      log("{} FAILED {} {}", prefix(desc), e, mfe.getFailures());
    }
    else {
      log("{} FAILED", prefix(desc), e);
    }
  }

  @Override
  public void finished(final Description desc) {
    log("{} FINISHED", prefix(desc));
  }
}