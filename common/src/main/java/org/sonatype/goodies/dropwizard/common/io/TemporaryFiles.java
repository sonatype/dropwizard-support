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
package org.sonatype.goodies.dropwizard.common.io;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * Temporary file helpers.
 *
 * @since ???
 */
public class TemporaryFiles
{
  private static final Logger log = LoggerFactory.getLogger(TemporaryFiles.class);

  private static Path baseDir;

  private TemporaryFiles() {
    // empty
  }

  /**
   * Resolve system temporary directory.
   */
  public synchronized static Path directory() throws IOException {
    if (baseDir == null) {
      String location = System.getProperty("java.io.tmpdir", "tmp");

      Path dir = new File(location).getCanonicalFile().toPath();
      FileHelper.createDirectories(dir);

      // ensure we can create temporary files in this directory
      Path file = Files.createTempFile(dir, "tmpprobe-", ".tmp");
      Files.delete(file);
      baseDir = dir;
    }
    return baseDir;
  }

  /**
   * Resolve temporary sub-directory under system temporary directory.
   */
  public static Path directory(final String name) throws IOException {
    checkNotNull(name);
    checkState(!name.startsWith("/"), "Invalid temporary directory name: %s", name);
    Path dir = directory().resolve(name);
    FileHelper.createDirectories(dir);
    return dir;
  }

  //
  // Builder
  //

  public static class Builder
  {
    private String prefix;

    private String suffix = ".tmp";

    private Path directory;

    private InputStream content;

    private Function<OutputStream, OutputStream> decorator;

    public Builder prefix(final String prefix) {
      this.prefix = prefix;
      return this;
    }

    public Builder suffix(final String suffix) {
      this.suffix = suffix;
      return this;
    }

    public Builder extension(final String extension) {
      return suffix("." + extension);
    }

    public Builder directory(final Path directory) {
      this.directory = directory;
      return this;
    }

    public Builder content(final InputStream content) {
      this.content = content;
      return this;
    }

    public Builder decorator(final Function<OutputStream, OutputStream> decorator) {
      this.decorator = decorator;
      return this;
    }

    public Path path() throws IOException {
      checkState(prefix != null, "Missing: prefix");
      if (decorator != null) {
        checkState(content != null, "Missing: content");
      }

      Path file;
      if (directory != null) {
        file = Files.createTempFile(directory, prefix, suffix);
      }
      else {
        file = Files.createTempFile(prefix, suffix);
      }
      log.trace("Created: {}", file);

      if (content != null) {
        try {
          try (OutputStream output = maybeDecorate(Files.newOutputStream(file, WRITE))) {
            // copy internally buffers
            long bytes = ByteStreams.copy(content, output);
            log.trace("Wrote: {} {} bytes", file, bytes);
          }
        }
        catch (IOException e) {
          // clean up temporary file if we failed to write content
          Files.deleteIfExists(file);
          throw e;
        }
      }

      return file;
    }

    private OutputStream maybeDecorate(final OutputStream source) {
      if (decorator != null) {
        return decorator.apply(source);
      }
      return source;
    }

    public File file() throws IOException {
      return path().toFile();
    }

    public InputStream stream() throws IOException {
      checkState(content != null, "Missing: content");
      final Path tmp = path();

      return new FilterInputStream(Files.newInputStream(tmp, READ))
      {
        @Override
        public void close() throws IOException {
          try {
            super.close();
          }
          finally {
            Files.deleteIfExists(tmp);
          }
        }
      };
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
