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
package org.sonatype.goodies.dropwizard.logging;

// Copied and adjusted from: https://github.com/jdillon/gossip/blob/master/gossip-bootstrap/src/main/java/com/planet57/gossip/Level.java

import org.slf4j.Logger;
import org.slf4j.spi.LocationAwareLogger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Logging level.
 *
 * @since 1.2.0
 */
public enum Level
{
  ALL(-1000),
  TRACE(LocationAwareLogger.TRACE_INT),
  DEBUG(LocationAwareLogger.DEBUG_INT),
  INFO(LocationAwareLogger.INFO_INT),
  WARN(LocationAwareLogger.WARN_INT),
  ERROR(LocationAwareLogger.ERROR_INT),
  OFF(1000);

  public final int id;

  Level(final int id) {
    this.id = id;
  }

  public boolean isEnabled(final Logger logger) {
    checkNotNull(logger);
    switch (this) {
      case ALL:
        return true;
      case TRACE:
        return logger.isTraceEnabled();
      case DEBUG:
        return logger.isDebugEnabled();
      case INFO:
        return logger.isInfoEnabled();
      case WARN:
        return logger.isWarnEnabled();
      case ERROR:
        return logger.isErrorEnabled();
      default:
        return false;
    }
  }

  public void log(final Logger logger, final String msg) {
    checkNotNull(logger);
    switch (this) {
      case TRACE:
        logger.trace(msg);
        break;
      case DEBUG:
        logger.debug(msg);
        break;
      case INFO:
        logger.info(msg);
        break;
      case WARN:
        logger.warn(msg);
        break;
      case ERROR:
        logger.error(msg);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public void log(final Logger logger, final String format, final Object arg) {
    checkNotNull(logger);
    switch (this) {
      case TRACE:
        logger.trace(format, arg);
        break;
      case DEBUG:
        logger.debug(format, arg);
        break;
      case INFO:
        logger.info(format, arg);
        break;
      case WARN:
        logger.warn(format, arg);
        break;
      case ERROR:
        logger.error(format, arg);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public void log(final Logger logger, final String format, final Object arg1, final Object arg2) {
    checkNotNull(logger);
    switch (this) {
      case TRACE:
        logger.trace(format, arg1, arg2);
        break;
      case DEBUG:
        logger.debug(format, arg1, arg2);
        break;
      case INFO:
        logger.info(format, arg1, arg2);
        break;
      case WARN:
        logger.warn(format, arg1, arg2);
        break;
      case ERROR:
        logger.error(format, arg1, arg2);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public void log(final Logger logger, final String format, final Object... args) {
    checkNotNull(logger);
    switch (this) {
      case TRACE:
        logger.trace(format, args);
        break;
      case DEBUG:
        logger.debug(format, args);
        break;
      case INFO:
        logger.info(format, args);
        break;
      case WARN:
        logger.warn(format, args);
        break;
      case ERROR:
        logger.error(format, args);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public void log(final Logger logger, final String msg, final Throwable cause) {
    checkNotNull(logger);
    switch (this) {
      case TRACE:
        logger.trace(msg, cause);
        break;
      case DEBUG:
        logger.debug(msg, cause);
        break;
      case INFO:
        logger.info(msg, cause);
        break;
      case WARN:
        logger.warn(msg, cause);
        break;
      case ERROR:
        logger.error(msg, cause);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }
}