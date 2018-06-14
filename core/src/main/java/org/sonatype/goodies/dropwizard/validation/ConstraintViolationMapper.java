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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.google.common.collect.ImmutableList;
import io.dropwizard.jersey.validation.ValidationErrorMessage;

/**
 * {@link ConstraintViolationMapper} exception-mapper.
 *
 * @since ???
 */
@Named
@Singleton
public class ConstraintViolationMapper
    implements ExceptionMapper<ConstraintViolationException>
{
  @Override
  public Response toResponse(final ConstraintViolationException exception) {
    Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();

    // TODO: consider more robust property-naming resolution
    List<String> errors = violations.stream()
        .map(v -> String.format("%s %s", v.getPropertyPath(), v.getMessage()))
        .collect(Collectors.toList());

    return Response.status(Status.BAD_REQUEST)
        .entity(new ValidationErrorMessage(ImmutableList.copyOf(errors)))
        .build();
  }
}
