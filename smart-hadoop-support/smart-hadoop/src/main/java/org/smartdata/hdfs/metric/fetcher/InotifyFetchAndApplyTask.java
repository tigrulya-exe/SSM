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
package org.smartdata.hdfs.metric.fetcher;

import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSInotifyEventInputStream;
import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.inotify.EventBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.SmartConstants;
import org.smartdata.conf.SmartConf;
import org.smartdata.metastore.MetaStore;
import org.smartdata.model.SystemInfo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class InotifyFetchAndApplyTask implements Runnable {
  static final Logger LOG = LoggerFactory.getLogger(InotifyFetchAndApplyTask.class);

  private final AtomicLong lastId;
  private final MetaStore metaStore;
  private final InotifyEventApplier applier;
  private final DFSInotifyEventInputStream inotifyEventInputStream;
  private final INotifyEventFilter eventFilter;

  public InotifyFetchAndApplyTask(DFSClient client, MetaStore metaStore,
      InotifyEventApplier applier, long startId, SmartConf conf) throws IOException {
    this.applier = applier;
    this.metaStore = metaStore;
    this.lastId = new AtomicLong(startId);
    this.inotifyEventInputStream = client.getInotifyEventStream(startId);
    this.eventFilter = new INotifyEventFilter(conf);
  }

  @Override
  public void run() {
    LOG.debug("InotifyFetchAndApplyTask run at {}", LocalDateTime.now());
    try {
      EventBatch eventBatch = inotifyEventInputStream.poll();
      while (eventBatch != null) {
        Event[] filteredEvents = eventFilter.filterIgnored(eventBatch.getEvents());
        if (filteredEvents.length != 0) {
          applier.apply(filteredEvents);
        }

        lastId.getAndSet(eventBatch.getTxid());
        metaStore.updateAndInsertIfNotExist(
            new SystemInfo(
                SmartConstants.SMART_HDFS_LAST_INOTIFY_TXID, String.valueOf(lastId.get())));
        eventBatch = inotifyEventInputStream.poll();
      }
    } catch (Throwable t) {
      LOG.error("Inotify Apply Events error", t);
    }
  }
}
