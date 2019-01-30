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

def targetDir = new File(project.build.directory)
ant.mkdir(dir: targetDir)

[ 'src/main/config/wro.properties', 'src/main/config/wro.xml' ].each { filename ->
  def file = new File(basedir, filename)
  ant.copy(file: file, todir: targetDir, filtering: true) {
    filterset {
      project.properties.each { key, value ->
        filter(token: key, value: value)
      }
    }
  }
}