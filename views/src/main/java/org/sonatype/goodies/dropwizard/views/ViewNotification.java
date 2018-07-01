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
package org.sonatype.goodies.dropwizard.views;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * View notification.
 *
 * @since ???
 */
public class ViewNotification
{
  public enum Type
  {
    ERROR,
    WARNING,
    SUCCESS
  }

  private final Type type;

  private final String message;

  public ViewNotification(final Type type, final String message) {
    this.type = checkNotNull(type);
    this.message = checkNotNull(message);
  }

  public Type getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public String getDisplayClass() {
    switch (type) {
      case ERROR:
        return "alert alert-danger";
      case WARNING:
        return "alert alert-warning";
      case SUCCESS:
        return "alert alert-success";
    }
    throw new RuntimeException("Invalid type: " + type);
  }

  public String getIconClass() {
    switch (type) {
      case ERROR:
        return "fas fa-exclamation-circle text-danger";
      case WARNING:
        return "fas fa-exclamation-triangle text-warning";
      case SUCCESS:
        return "fas fa-check-circle text-success";
    }
    throw new RuntimeException("Invalid type: " + type);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("type", type)
        .add("message", message)
        .toString();
  }

  public static ViewNotification error(final String message) {
    return new ViewNotification(Type.ERROR, message);
  }

  public static ViewNotification warning(final String message) {
    return new ViewNotification(Type.WARNING, message);
  }

  public static ViewNotification success(final String message) {
    return new ViewNotification(Type.SUCCESS, message);
  }
}
