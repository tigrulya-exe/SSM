/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.model;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class WhitelistHelperTest {

  @Test
  public void testThrowIfPathIsUncovered() {
    PathChecker pathChecker = pathCheckerFor("/covered/", "/another_dir/");

    List<String> paths = Arrays.asList(
        "/covered/file1",
        "/covered/file2/",
        "/uncovered",
        "/another_dir/1");

    IOException exception = assertThrows(
        IOException.class,
        () -> WhitelistHelper.validatePathsCovered(paths, pathChecker));

    assertEquals("Path /uncovered/ is not in the whitelist.", exception.getMessage());
  }

  @Test
  public void testDontThrowIfPathIsCovered() throws IOException {
    PathChecker pathChecker = pathCheckerFor("/covered/", "/another_dir/");

    List<String> paths = Arrays.asList(
        "/covered/file1",
        "/covered/file2/",
        "/another_dir/1");

    WhitelistHelper.validatePathsCovered(paths, pathChecker);
  }

  @Test
  public void testDontThrowIfCoverDirsAreEmpty() throws IOException {
    PathChecker pathChecker = pathCheckerFor();

    List<String> paths = Arrays.asList(
        "/covered/file1",
        "/any/path/",
        "/other/path/",
        "file",
        "/another_dir/1");

    WhitelistHelper.validatePathsCovered(paths, pathChecker);
  }

  private PathChecker pathCheckerFor(String... coverDirs) {
    return new PathChecker(Collections.emptyList(), Arrays.asList(coverDirs));
  }
}
