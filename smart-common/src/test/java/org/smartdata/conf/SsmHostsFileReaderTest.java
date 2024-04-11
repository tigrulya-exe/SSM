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
package org.smartdata.conf;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SsmHostsFileReaderTest {
  private final SsmHostsFileReader hostsFileReader = new SsmHostsFileReader();

  @Test
  public void testReadHostsFile() throws IOException {
    Set<String> parsedHosts = hostsFileReader.parse(
        hostsFilePathFromResources("servers"));
    Set<String> expectedHosts = Sets.newHashSet("host1", "host2", "host3");

    assertEquals(expectedHosts, parsedHosts);
  }

  @Test
  public void testReplaceLocalhost() throws IOException {
    Set<String> parsedHosts = hostsFileReader.parse(
        hostsFilePathFromResources("agents-with-localhost"));

    assertEquals(parsedHosts.size(), 2);
    assertTrue(parsedHosts.contains("host1"));
    assertFalse(parsedHosts.contains("localhost"));
  }

  @Test
  public void testReadEmptyHostsFile() throws IOException {
    Set<String> parsedHosts = hostsFileReader.parse(
        hostsFilePathFromResources("empty-hosts"));

    assertTrue(parsedHosts.isEmpty());
  }

  private Path hostsFilePathFromResources(String fileName) {
    return Optional.ofNullable(getClass().getClassLoader().getResource("conf"))
        .map(confDir -> Paths.get(confDir.getPath(), fileName))
        .orElseThrow(() -> new RuntimeException("Resource not found"));
  }
}
