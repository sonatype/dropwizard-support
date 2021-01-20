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

import java.nio.file.FileStore;
import java.nio.file.FileSystems;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.slf4j.Logger;

/**
 * Filesystem {@link EnvironmentReport.Section}.
 *
 * @since ???
 */
@JsonTypeName(FilesystemSection.TYPE)
public class FilesystemSection
  extends EnvironmentReport.Section
{
  public static final String TYPE = "filesystem";

  public FilesystemSection() {
    super(TYPE);
  }

  @Override
  public void render(final Logger logger) throws Exception {
    for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
      try {
        log(logger, "File-store; name={}, type={}, total={}, usable={}, unallocated={}, read-only={}",
            fileStore.name(),
            fileStore.type(),
            fileStore.getTotalSpace(),
            fileStore.getUsableSpace(),
            fileStore.getUnallocatedSpace(),
            fileStore.isReadOnly()
        );
      }
      catch (Exception e) {
        logger.warn("Failed to query file-store: name={}, type={}", fileStore.name(), fileStore.type(), e);
      }
    }
  }
}
