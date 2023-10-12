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
package org.smartdata.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;

import java.io.IOException;
import java.util.ServiceLoader;

public interface MiniClusterFactory {

  class DefaultMiniClusterFactory implements MiniClusterFactory {
    @Override
    public MiniDFSCluster create(int dataNodes, Configuration conf) throws IOException {
      return new MiniDFSCluster.Builder(conf).numDataNodes(dataNodes).build();
    }

    @Override
    public MiniDFSCluster createWithStorages(int dataNodes, Configuration conf) {
      throw new UnsupportedOperationException(
          "DefaultMiniClusterFactory does not support creating cluster with storage types");
    }
  }

  static MiniClusterFactory get() {
    ServiceLoader<MiniClusterFactory> loader = ServiceLoader.load(MiniClusterFactory.class);
    for (MiniClusterFactory factory : loader) {
      return factory;
    }

    return new MiniClusterFactory.DefaultMiniClusterFactory();
  }

  MiniDFSCluster create(int dataNodes, Configuration conf) throws IOException;

  MiniDFSCluster createWithStorages(int dataNodes, Configuration conf) throws IOException;
}
