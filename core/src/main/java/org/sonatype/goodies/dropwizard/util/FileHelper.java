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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * File helpers.
 *
 * @since 1.2.0
 */
public class FileHelper
{
  private FileHelper() {
    // empty
  }

  private static final Logger log = LoggerFactory.getLogger(FileHelper.class);

  /**
   * Return canonical file; rethrowing any exceptions as unchecked.
   *
   * @since 1.0.4
   */
  public static File canonical(final File file) {
    checkNotNull(file);
    try {
      return file.getCanonicalFile();
    }
    catch (IOException e) {
      log.warn("Failed to canonicalize file: {}", file, e);

      // rethrow
      throw new RuntimeException(e);
    }
  }

  /**
   * Copy source file to target file.
   *
   * Will replace existing target file and ensure target directory structure.
   *
   * @since 1.0.4
   */
  public static void copy(final File source, final File target) throws IOException {
    checkNotNull(source);
    checkNotNull(target);

    log.trace("Copy: {} -> {}", source, target);
    Files.createDirectories(target.getParentFile().toPath());
    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * Extract filename from given path.
   *
   * @since 1.0.4
   */
  public static String filename(final String path) {
    checkNotNull(path);
    return Paths.get(path).getFileName().toString();
  }

  /**
   * Extract file extension from given path; sans-leading {@literal .}.
   *
   * @since 1.0.4
   */
  public static String extension(final String path) {
    checkNotNull(path);
    int i = path.lastIndexOf('.');
    if (i == -1) {
      return "";
    }
    return path.substring(i + 1);
  }

  /**
   * Attempt to delete given file.
   *
   * @since 1.0.4
   */
  public static void delete(final File file) {
    checkNotNull(file);
    if (file.exists()) {
      try {
        log.trace("Delete: {}", file);
        Files.delete(file.toPath());
      }
      catch (IOException e) {
        log.warn("Failed to delete file: {}", file, e);
        // gobble exception
      }
    }
  }

  /**
   * Delete given files if non-null.
   *
   * @since 1.0.4
   */
  public static void delete(final File... files) {
    checkNotNull(files);
    for (File file : files) {
      if (file != null) {
        FileHelper.delete(file);
      }
    }
  }

  /**
   * Attempt to delete given file.
   *
   * @since ???
   */
  public static void delete(final Path file) {
    checkNotNull(file);
    try {
      Files.deleteIfExists(file);
    }
    catch (IOException e) {
      log.warn("Failed to delete file: {}", file, e);
      // gobble exception
    }
  }

  /**
   * Delete given files if non-null.
   *
   * @since ???
   */
  public static void delete(final Path... files) {
    checkNotNull(files);
    for (Path file : files) {
      if (file != null) {
        FileHelper.delete(file);
      }
    }
  }


  /**
   * Resolve file for given path.
   */
  public static File resolveFile(final String path) {
    checkNotNull(path);
    return canonical(new File(path));
  }

  /**
   * Resolve path.
   */
  public static String resolvePath(final String path) {
    return resolveFile(path).getPath();
  }

  /**
   * Return resolved temporary directory.
   *
   * @since 1.2.0
   */
  public static File tmpdir() {
    return resolveFile(System.getProperty("java.io.tmpdir"));
  }

  /**
   * Delete a directory.
   *
   * @since ???
   */
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

  /**
   * Create directories.
   *
   * @since ???
   */
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
