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
package org.smartdata.hdfs.client;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.smartdata.hdfs.HadoopUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Slf4j
public abstract class BaseFileSystemCache<T extends DistributedFileSystem> implements FileSystemCache<T> {

  private final Cache<CacheKey, T> fileSystemCache;
  private final ScheduledExecutorService evictionHandlerExecutor;

  public BaseFileSystemCache(Duration keyTtl) {
    this.evictionHandlerExecutor = Executors.newSingleThreadScheduledExecutor();
    this.fileSystemCache = Caffeine.newBuilder()
        .expireAfterAccess(keyTtl)
        .removalListener(this::onEntryRemoved)
        .scheduler(Scheduler.forScheduledExecutorService(evictionHandlerExecutor))
        .build();
  }

  @Override
  public T get(Configuration config, InetSocketAddress ssmMasterAddress)
      throws IOException {
    CacheKey cacheKey = new CacheKey(ssmMasterAddress, HadoopUtil.getNameNodeUri(config));
    return fileSystemCache.get(cacheKey, key -> createFileSystem(config, key));
  }

  @Override
  public void close() throws IOException {
    fileSystemCache.invalidateAll();
    fileSystemCache.cleanUp();
    evictionHandlerExecutor.shutdown();
  }

  protected abstract T createFileSystem(Configuration config, CacheKey cacheKey);

  private void onEntryRemoved(CacheKey key, T value, RemovalCause removalCause) {
    Optional.ofNullable(value).ifPresent(this::closeFileSystem);
  }

  private void closeFileSystem(T fileSystem) {
    try {
      fileSystem.close();
    } catch (IOException exception) {
      log.error("Error closing cached fileSystem after expiration", exception);
    }
  }

  @Data
  protected static class CacheKey {
    private final InetSocketAddress ssmMasterAddress;
    private final URI nameNodeUri;
  }
}
