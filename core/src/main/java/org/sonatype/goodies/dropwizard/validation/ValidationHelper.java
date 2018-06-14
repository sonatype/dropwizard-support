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
package org.sonatype.goodies.dropwizard.validation;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Validation helper.
 *
 * @since ???
 */
@Named
@Singleton
public class ValidationHelper
{
  private static final Logger log = LoggerFactory.getLogger(ValidationHelper.class);

  private final Validator validator;

  @Inject
  public ValidationHelper(final Validator validator) {
    this.validator = checkNotNull(validator);
  }

  /**
   * Validate given value.
   *
   * @throws ConstraintViolationException if any violations are discovered.
   */
  public <T> T validate(final T value) {
    log.debug("Validate: {}", value);

    Set<ConstraintViolation<T>> violations = validator.validate(value);
    if (!violations.isEmpty()) {
      if (log.isDebugEnabled()) {
        log.debug("Constraint violations: {}", violations.size());
        for (ConstraintViolation violation : violations) {
          log.debug("  {}", violation);
        }
      }
      throw new ConstraintViolationException(violations);
    }

    return value;
  }
}
