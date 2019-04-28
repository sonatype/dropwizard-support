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

import org.junit.Before;
import org.junit.Rule;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

// copied from: https://github.com/sonatype/goodies/blob/master/testsupport/src/main/java/org/sonatype/goodies/testsupport/TestSupport.java

/**
 * Support for tests.
 *
 * @since ???
 * @deprecated Provided for compatibility only.
 */
@Deprecated
public class TestSupport
{
  protected final TestUtil util = new TestUtil(this);

  protected final Logger logger = util.getLog();

  @Rule
  public final TestTracer tracer = new TestTracer(this);

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  protected void log(final String message) {
    logger.info(message);
  }

  protected void log(final Object value) {
    logger.info(String.valueOf(value));
  }

  protected void log(final String format, final Object... args) {
    logger.info(format, args);
  }

  protected void log(final String message, final Throwable cause) {
    logger.info(message, cause);
  }
}