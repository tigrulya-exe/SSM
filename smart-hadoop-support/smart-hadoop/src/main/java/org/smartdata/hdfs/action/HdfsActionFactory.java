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
package org.smartdata.hdfs.action;

import org.smartdata.action.AbstractActionFactory;

/**
 * Built-in smart actions for HDFS system.
 */
public class HdfsActionFactory extends AbstractActionFactory {
  static {
    addAction(AllSsdFileAction.class);
    addAction(AllDiskFileAction.class);
    addAction(OneSsdFileAction.class);
    addAction(OneDiskFileAction.class);
    addAction(RamDiskFileAction.class);
    addAction(ArchiveFileAction.class);
    addAction(CacheFileAction.class);
    addAction(UncacheFileAction.class);
    addAction(ReadFileAction.class);
    addAction(WriteFileAction.class);
    addAction(CheckStorageAction.class);
    addAction(SetXAttrAction.class);
    addAction(CopyFileAction.class);
    addAction(CopyDirectoryAction.class);
    addAction(DeleteFileAction.class);
    addAction(RenameFileAction.class);
    addAction(ListFileAction.class);
    addAction(ConcatFileAction.class);
    addAction(AppendFileAction.class);
    addAction(MergeFileAction.class);
    addAction(MetaDataAction.class);
    addAction(Copy2S3Action.class);
    addAction(CompressionAction.class);
    addAction(DecompressionAction.class);
    addAction(CheckCompressAction.class);
    addAction(TruncateAction.class);
    addAction(SmallFileCompactAction.class);
    addAction(SmallFileUncompactAction.class);
    addAction(CheckSumAction.class);
    addAction(DistCpAction.class);
    addAction(ListErasureCodingPolicy.class);
    addAction(CheckErasureCodingPolicy.class);
    addAction(ErasureCodingAction.class);
    addAction(UnErasureCodingAction.class);
    addAction(AddErasureCodingPolicy.class);
    addAction(RemoveErasureCodingPolicy.class);
    addAction(EnableErasureCodingPolicy.class);
    addAction(DisableErasureCodingPolicy.class);
  }
}
