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
package org.sonatype.goodies.dropwizard.testsupport;

import org.sonatype.goodies.dropwizard.ApplicationVersion;

/**
 * Test {@link ApplicationVersion}.
 *
 * @since 1.0.2
 */
public class TestApplicationVersion
    implements ApplicationVersion
{
  private String version = "1-TEST";

  private String buildTimestamp = String.valueOf(System.currentTimeMillis());

  private String buildTag = "TEST";

  private String buildNotes = "Test";

  @Override
  public String getVersion() {
    return version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

  @Override
  public String getBuildTimestamp() {
    return buildTimestamp;
  }

  public void setBuildTimestamp(final String buildTimestamp) {
    this.buildTimestamp = buildTimestamp;
  }

  @Override
  public String getBuildTag() {
    return buildTag;
  }

  public void setBuildTag(final String buildTag) {
    this.buildTag = buildTag;
  }

  @Override
  public String getBuildNotes() {
    return buildNotes;
  }

  public void setBuildNotes(final String buildNotes) {
    this.buildNotes = buildNotes;
  }
}
