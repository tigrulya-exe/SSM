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
package org.smartdata.protocol.protobuffer;

import com.google.protobuf.ServiceException;
import org.smartdata.metrics.FileAccessEvent;
import org.smartdata.model.CompactFileState;
import org.smartdata.model.CompressionFileState;
import org.smartdata.model.FileContainerInfo;
import org.smartdata.model.FileState;
import org.smartdata.model.NormalFileState;
import org.smartdata.model.RuleState;
import org.smartdata.protocol.ClientServerProto.CompactFileStateProto;
import org.smartdata.protocol.ClientServerProto.CompressionFileStateProto;
import org.smartdata.protocol.ClientServerProto.FileStateProto;
import org.smartdata.protocol.ClientServerProto.ReportFileAccessEventRequestProto;

import java.io.IOException;
import java.util.Arrays;

public class ProtoBufferHelper {
  private ProtoBufferHelper() {
  }

  public static IOException getRemoteException(ServiceException se) {
    Throwable e = se.getCause();
    if (e == null) {
      return new IOException(se);
    }
    return e instanceof IOException ? (IOException) e : new IOException(se);
  }

  public static int convert(RuleState state) {
    return state.getValue();
  }

  public static RuleState convert(int state) {
    return RuleState.fromValue(state);
  }

  public static ReportFileAccessEventRequestProto convert(FileAccessEvent event) {
    return ReportFileAccessEventRequestProto.newBuilder()
        .setFilePath(event.getPath())
        .setAccessedBy(event.getAccessedBy())
        .setFileId(event.getFileId())
        .build();
  }

  public static FileAccessEvent convert(final ReportFileAccessEventRequestProto event) {
    return new FileAccessEvent(event.getFilePath(), 0, event.getAccessedBy());
  }

  private static FileContainerInfo convert(CompactFileStateProto proto) {
    String containerFilePath = proto.getContainerFilePath();
    long offset = proto.getOffset();
    long length = proto.getLength();
    return new FileContainerInfo(containerFilePath, offset, length);
  }

  public static FileState convert(FileStateProto proto) {
    FileState fileState = null;
    String path = proto.getPath();
    FileState.FileType type = FileState.FileType.fromValue(proto.getType());
    FileState.FileStage stage = FileState.FileStage.fromValue(proto.getStage());
    if (type == null) {
      return new NormalFileState(path);
    }
    switch (type) {
      case NORMAL:
        fileState = new NormalFileState(path);
        break;
      case COMPACT:
        CompactFileStateProto compactProto = proto.getCompactFileState();
        fileState = new CompactFileState(path, convert(compactProto));
        break;
      case COMPRESSION:
        CompressionFileStateProto compressionProto = proto.getCompressionFileState();
        // convert to CompressionFileState
        fileState = convert(path, stage, compressionProto);
        break;
      default:
    }
    return fileState;
  }

  public static FileStateProto convert(FileState fileState) {
    FileStateProto.Builder builder = FileStateProto.newBuilder()
        .setPath(fileState.getPath())
        .setType(fileState.getFileType().getValue())
        .setStage(fileState.getFileStage().getValue());
    if (fileState instanceof CompactFileState) {
      FileContainerInfo fileContainerInfo = (
          (CompactFileState) fileState).getFileContainerInfo();
      builder.setCompactFileState(CompactFileStateProto.newBuilder()
          .setContainerFilePath(fileContainerInfo.getContainerFilePath())
          .setOffset(fileContainerInfo.getOffset())
          .setLength(fileContainerInfo.getLength()));
    } else if (fileState instanceof CompressionFileState) {
      builder.setCompressionFileState(convert((CompressionFileState) fileState));
    }
    return builder.build();
  }

  public static CompressionFileState convert(String path,
                                             FileState.FileStage stage,
                                             CompressionFileStateProto proto) {
    return CompressionFileState.newBuilder()
        .setFileName(path)
        .setFileStage(stage)
        .setBufferSize(proto.getBufferSize())
        .setCompressImpl(proto.getCompressionImpl())
        .setOriginalLength(proto.getOriginalLength())
        .setCompressedLength(proto.getCompressedLength())
        .setOriginalPos(proto.getOriginalPosList())
        .setCompressedPos(proto.getCompressedPosList())
        .build();
  }

  public static CompressionFileStateProto convert(CompressionFileState fileState) {
    return CompressionFileStateProto.newBuilder()
        .setBufferSize(fileState.getBufferSize())
        .setCompressionImpl(fileState.getCompressionImpl())
        .setOriginalLength(fileState.getOriginalLength())
        .setCompressedLength(fileState.getCompressedLength())
        .addAllOriginalPos(Arrays.asList(fileState.getOriginalPos()))
        .addAllCompressedPos(Arrays.asList(fileState.getCompressedPos()))
        .build();
  }
}
