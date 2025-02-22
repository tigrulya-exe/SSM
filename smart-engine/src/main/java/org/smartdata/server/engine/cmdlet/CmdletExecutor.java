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
package org.smartdata.server.engine.cmdlet;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.smartdata.conf.SmartConf;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.model.CmdletState;
import org.smartdata.protocol.message.ActionStatus;
import org.smartdata.protocol.message.StatusReport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//Todo: 1. make this a interface so that we could have different executor implementation
//      2. add api providing available resource
public class CmdletExecutor {
  private final Map<Long, Future<?>> listenableFutures;
  private final Map<Long, Cmdlet> runningCmdlets;
  private final Map<Long, Cmdlet> idToReportCmdlet;

  private final ListeningExecutorService executorService;

  public CmdletExecutor(SmartConf smartConf) {
    this.listenableFutures = new ConcurrentHashMap<>();
    this.runningCmdlets = new ConcurrentHashMap<>();
    this.idToReportCmdlet = new ConcurrentHashMap<>();
    int cmdletExecutorsNum =
        smartConf.getInt(
            SmartConfKeys.SMART_CMDLET_EXECUTORS_KEY,
            SmartConfKeys.SMART_CMDLET_EXECUTORS_DEFAULT);
    this.executorService = MoreExecutors.listeningDecorator(
        Executors.newFixedThreadPool(cmdletExecutorsNum));
  }

  public void execute(Cmdlet cmdlet) {
    ListenableFuture<?> future = executorService.submit(cmdlet);
    Futures.addCallback(future, new CmdletCallBack(cmdlet), executorService);
    listenableFutures.put(cmdlet.getId(), future);
    runningCmdlets.put(cmdlet.getId(), cmdlet);
    idToReportCmdlet.put(cmdlet.getId(), cmdlet);
  }

  public void stop(Long cmdletId) {
    if (listenableFutures.containsKey(cmdletId)) {
      runningCmdlets.get(cmdletId).setState(CmdletState.FAILED);
      listenableFutures.get(cmdletId).cancel(true);
    }
    removeCmdlet(cmdletId);
  }

  public void shutdown() {
    executorService.shutdown();
  }

  public StatusReport getStatusReport() {
    if (idToReportCmdlet.isEmpty()) {
      return null;
    }

    List<ActionStatus> actionStatusList = new ArrayList<>();
    Iterator<Cmdlet> iter = idToReportCmdlet.values().iterator();
    while (iter.hasNext()) {
      Cmdlet cmdlet = iter.next();
      List<ActionStatus> statuses = cmdlet.getActionStatuses();
      if (statuses != null) {
        actionStatusList.addAll(statuses);
      } else {
        iter.remove();
      }
    }

    return new StatusReport(actionStatusList);
  }

  private void removeCmdlet(long cmdletId) {
    runningCmdlets.remove(cmdletId);
    listenableFutures.remove(cmdletId);
  }

  private class CmdletCallBack implements FutureCallback<Object> {
    private final Cmdlet cmdlet;

    public CmdletCallBack(Cmdlet cmdlet) {
      this.cmdlet = cmdlet;
    }

    @Override
    public void onSuccess(Object result) {
      removeCmdlet(cmdlet.getId());
    }

    @Override
    public void onFailure(Throwable t) {
      removeCmdlet(cmdlet.getId());
    }
  }

}
