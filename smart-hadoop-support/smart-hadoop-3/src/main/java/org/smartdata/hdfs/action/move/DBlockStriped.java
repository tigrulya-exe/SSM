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
package org.smartdata.hdfs.action.move;

import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.util.StripedBlockUtil;

public class DBlockStriped extends DBlock {

  final byte[] indices;
  final short dataBlockNum;
  final int cellSize;

  public DBlockStriped(Block block, byte[] indices, short dataBlockNum,
                       int cellSize) {
    super(block);
    this.indices = indices;
    this.dataBlockNum = dataBlockNum;
    this.cellSize = cellSize;
  }

  public DBlock getInternalBlock(StorageGroup storage) {
    int idxInLocs = locations.indexOf(storage);
    if (idxInLocs == -1) {
      return null;
    }
    byte idxInGroup = indices[idxInLocs];
    long blkId = getBlock().getBlockId() + idxInGroup;
    long numBytes = StripedBlockUtil.getInternalBlockLength(getNumBytes(), cellSize,
        dataBlockNum, idxInGroup);
    Block blk = new Block(getBlock());
    blk.setBlockId(blkId);
    blk.setNumBytes(numBytes);
    DBlock dblk = new DBlock(blk);
    dblk.addLocation(storage);
    return dblk;
  }
}
