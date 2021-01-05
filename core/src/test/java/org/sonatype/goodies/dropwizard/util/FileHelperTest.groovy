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
package org.sonatype.goodies.dropwizard.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow
import static org.junit.jupiter.api.Assertions.assertThrows

/**
 * {@link FileHelper} tests.
 */
class FileHelperTest
{
  @Test
  void 'new-file directory-traversal prevention'(@TempDir final File basedir) {
    println basedir

    assertThrows(IllegalStateException.class, {
      FileHelper.newFile(basedir, 'traversal/happens/../../../here')
    })

    assertDoesNotThrow({
      FileHelper.newFile(basedir, 'traversal/does/not/happen/../../../here')
    } as Executable)
  }
}
