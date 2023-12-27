/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartdata.hdfs.action;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.tools.DistCpOptions;
import org.junit.Assert;
import org.junit.Test;
import org.smartdata.hdfs.MiniClusterHarness;

/**
 * Test for DistCpAction.
 * TODO: idents 2 spaces
 */
public class TestDistCpAction extends MiniClusterHarness {

    public DistCpAction createAction(Map<String, String> args) {
        DistCpAction distCpAction = new DistCpAction();
        distCpAction.setDfsClient(dfsClient);
        distCpAction.setContext(smartContext);
        distCpAction.init(args);
        return distCpAction;
    }

    @Test
    public void testParseSingleSource() {
        Map<String, String> args = new HashMap<>();
        args.put(DistCpAction.FILE_PATH, "/test/source/dir1");
        args.put(DistCpAction.TARGET_ARG, "hdfs://nn2/test/target/dir1");
        DistCpAction action = createAction(args);
        DistCpOptions distCpOptions = action.getOptions();

        Path expectedSource = new Path("/test/source/dir1");
        Assert.assertEquals(Collections.singletonList(expectedSource),
            distCpOptions.getSourcePaths());
        Assert.assertEquals(new Path("hdfs://nn2/test/target/dir1"),
            distCpOptions.getTargetPath());
    }

    @Test
    public void testParseSeveralSources() {
        Map<String, String> args = new HashMap<>();
        args.put(DistCpAction.FILE_PATH, "/test/source/dir1,/test/source/dir2,/test/source/dir3");
        args.put(DistCpAction.TARGET_ARG, "hdfs://nn2/test/target/dir1");
        DistCpAction action = createAction(args);
        DistCpOptions distCpOptions = action.getOptions();

        List<Path> expectedSources = Stream.of(
            "/test/source/dir1", "/test/source/dir2", "/test/source/dir3")
            .map(Path::new)
            .collect(Collectors.toList());
        Assert.assertEquals(expectedSources,
            distCpOptions.getSourcePaths());
        Assert.assertEquals(new Path("hdfs://nn2/test/target/dir1"),
            distCpOptions.getTargetPath());
    }

    @Test
    public void testIntraClusterCopy() throws Exception {
        Map<String, String> args = new HashMap<>();
        args.put(DistCpAction.FILE_PATH, "/test/source/dir1");
        args.put(DistCpAction.TARGET_ARG, dfs.getUri() + "/test/target/dir1");
        DistCpAction action = createAction(args);

        action.execute();
    }
}
