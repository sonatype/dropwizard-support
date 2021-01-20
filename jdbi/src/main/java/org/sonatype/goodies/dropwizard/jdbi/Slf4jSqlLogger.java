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
package org.sonatype.goodies.dropwizard.jdbi;

import java.sql.SQLException;

import org.sonatype.goodies.dropwizard.util.Level;

import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Slf4j {@link SqlLogger} adapter.
 *
 * @since 1.2.0
 */
public class Slf4jSqlLogger
    implements SqlLogger
{
  private final Logger logger;

  private final Level level;

  public Slf4jSqlLogger(final Logger logger, final Level level) {
    this.logger = checkNotNull(logger);
    this.level = checkNotNull(level);
  }

  @Override
  public void logBeforeExecution(final StatementContext context) {
    level.log(logger, "Before-execution; SQL: {}, Parsed: {}; Attributes: {}",
        context.getRenderedSql(),
        context.getParsedSql(),
        context.getAttributes()
    );
  }

  @Override
  public void logException(final StatementContext context, final SQLException e) {
    logger.warn("Exception", e);
  }
}