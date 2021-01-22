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

import org.sonatype.goodies.dropwizard.logging.Level;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Discoverable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Environment report.
 *
 * @since ???
 * @see Section
 */
public class EnvironmentReport
{
  private static final Logger log = LoggerFactory.getLogger(EnvironmentReport.class);

  @NotNull
  @JsonProperty
  private Level level = Level.INFO;

  public Level getLevel() {
    return level;
  }

  public void setLevel(final Level level) {
    this.level = checkNotNull(level);
  }

  /**
   * Environment-report section.
   */
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

    public abstract void render(Logger logger) throws Exception;

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

  public void render(final Logger logger) {
    log.trace("Render {} sections", sections.size());
    for (Section section : sections) {
      // if section has no level; inherit from report
      Level level = section.getLevel();
      if (level == null) {
        level = this.level;
        section.setLevel(level);
      }
      if (level.isEnabled(logger)) {
        log.trace("Render section: {} as {}", section, level);
        try {
          section.render(logger);
        }
        catch (Exception e) {
          log.warn("Failed to render section: {}", section, e);
        }
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
