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
package org.sonatype.goodies.dropwizard.version;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Version.
 *
 * @since ???
 */
public class Version
    implements Serializable
{
  private static final long serialVersionUID = 1L;

  @JsonProperty
  private String version;

  @JsonProperty
  private String timestamp;

  @JsonProperty
  private String tag;

  @JsonProperty
  private String notes;

  public Version(final String version, final String timestamp, final String tag, final String notes) {
    this.version = version;
    this.timestamp = timestamp;
    this.tag = tag;
    this.notes = notes;
  }

  public Version() {
    // empty
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final String timestamp) {
    this.timestamp = timestamp;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(final String tag) {
    this.tag = tag;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(final String notes) {
    this.notes = notes;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Version version1 = (Version) o;
    return Objects.equals(version, version1.version) &&
        Objects.equals(timestamp, version1.timestamp) &&
        Objects.equals(tag, version1.tag) &&
        Objects.equals(notes, version1.notes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, timestamp, tag, notes);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("version", version)
        .add("timestamp", timestamp)
        .add("tag", tag)
        .add("notes", notes)
        .toString();
  }
}
