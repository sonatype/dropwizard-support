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
package org.sonatype.goodies.dropwizard.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

// TODO: merge with FileHelper

/**
 * File helpers.
 *
 * @since ???
 */
public class FilesHelper
{
  private static final Logger log = LoggerFactory.getLogger(FilesHelper.class);

  private FilesHelper() {
    // empty
  }

  public static void deleteDirectory(final Path dir) throws IOException {
    checkNotNull(dir);
    log.trace("Delete directory: {}", dir);

    // skip if directory does not exist
    if (!Files.exists(dir)) {
      return;
    }

    checkState(Files.isDirectory(dir), "Not a directory: %s", dir);

    Files.walk(dir)
        .sorted(Comparator.reverseOrder())
        .forEach(file -> {
          try {
            Files.deleteIfExists(file);
          }
          catch (IOException e) {
            log.warn("Failed to delete file: {}", file, e);
          }
        });
  }

  public static void createDirectories(final Path dir) throws IOException {
    checkNotNull(dir);
    log.trace("Create directories: {}", dir);

    if (!Files.exists(dir)) {
      Files.createDirectories(dir);
      log.trace("Created: {}", dir);
    }
    else {
      checkState(Files.isDirectory(dir), "Not directory: %s", dir);
      checkState(Files.isWritable(dir), "Directory not writable: %s", dir);
    }
  }
}
