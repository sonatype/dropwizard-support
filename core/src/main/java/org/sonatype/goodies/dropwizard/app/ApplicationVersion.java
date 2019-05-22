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
package org.sonatype.goodies.dropwizard.app;

/**
 * Provides application version information.
 *
 * @since 1.0.0
 */
public interface ApplicationVersion
{
  /**
   * Returns the application version.
   */
  String getVersion();

  /**
   * Returns the application build-timestamp.
   */
  String getBuildTimestamp();

  /**
   * Returns the application build-tag.
   */
  String getBuildTag();

  /**
   * Returns application build-notes.
   *
   * @since 1.0.2
   */
  String getBuildNotes();
}
