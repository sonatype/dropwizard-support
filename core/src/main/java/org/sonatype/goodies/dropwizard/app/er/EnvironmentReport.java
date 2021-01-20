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
package org.sonatype.goodies.dropwizard.app.er;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.sonatype.goodies.dropwizard.util.Level;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Discoverable;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Environment report.
 *
 * @since ???
 * @see Section
 */
public class EnvironmentReport
{
  @NotNull
  @JsonProperty
  private Level level = Level.INFO;

  public Level getLevel() {
    return level;
  }

  public void setLevel(final Level level) {
    this.level = checkNotNull(level);
  }

  @JsonTypeInfo(use = Id.NAME, property = "type")
  public abstract static class Section
      implements Discoverable
  {
    private final String type;

    public Section(final String type) {
      this.type = checkNotNull(type);
    }

    @Nullable
    @JsonProperty
    private Level level;

    @Nullable
    public Level getLevel() {
      return level;
    }

    public void setLevel(@Nullable final Level level) {
      this.level = level;
    }

    protected void log(final Logger logger, final String format, final Object... arguments) {
      assert level != null;
      level.log(logger, format, arguments);
    }

    public abstract void report(Logger logger) throws Exception;

    @Override
    public String toString() {
      return type;
    }
  }

  @NotNull
  @JsonProperty
  private List<Section> sections = new ArrayList<>();

  public List<Section> getSections() {
    return sections;
  }

  public void setSections(final List<Section> sections) {
    this.sections = checkNotNull(sections);
  }

  public void report(final Logger logger) {
    logger.trace("Report {} sections", sections.size());
    for (Section section : sections) {
      // if section has no level; inherit from report
      Level level = section.getLevel();
      if (level == null) {
        level = this.level;
        section.setLevel(level);
      }
      logger.trace("Report section: {} as {}", section, level);
      if (!level.isEnabled(logger)) {
        continue;
      }
      try {
        section.report(logger);
      }
      catch (Exception e) {
        logger.warn("Failed to report section: {}", section, e);
      }
    }
  }

  //
  // Factory
  //

  public static EnvironmentReport createDefault() {
    EnvironmentReport report = new EnvironmentReport();
    report.setLevel(Level.INFO);
    report.setSections(ImmutableList.of(
        new BasicSection()
    ));
    return report;
  }
}
