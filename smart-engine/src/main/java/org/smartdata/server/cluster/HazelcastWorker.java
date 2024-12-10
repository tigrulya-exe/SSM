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
package org.smartdata.server.cluster;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.SmartContext;
import org.smartdata.action.ActionException;
import org.smartdata.conf.SmartConf;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.hdfs.impersonation.UserImpersonationStrategy;
import org.smartdata.model.CmdletState;
import org.smartdata.protocol.message.CmdletStatusUpdate;
import org.smartdata.protocol.message.LaunchCmdlet;
import org.smartdata.protocol.message.StatusMessage;
import org.smartdata.protocol.message.StatusReporter;
import org.smartdata.protocol.message.StopCmdlet;
import org.smartdata.server.engine.cmdlet.CmdletExecutor;
import org.smartdata.server.engine.cmdlet.CmdletFactory;
import org.smartdata.server.engine.cmdlet.HazelcastExecutorService;
import org.smartdata.server.engine.cmdlet.StatusReportTask;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Todo: recover and reconnect when master is offline
@Slf4j
public class HazelcastWorker implements StatusReporter {
  private static final Logger LOG = LoggerFactory.getLogger(HazelcastWorker.class);
  private final ScheduledExecutorService executorService;
  private final ITopic<StatusMessage> statusTopic;
  private final CmdletExecutor cmdletExecutor;
  private final CmdletFactory factory;
  private final SmartConf smartConf;

  public HazelcastWorker(SmartContext smartContext) {
    this.smartConf = smartContext.getConf();
    UserImpersonationStrategy userImpersonationStrategy = UserImpersonationStrategy.from(smartConf);
    this.factory = new CmdletFactory(smartContext, userImpersonationStrategy);
    this.cmdletExecutor = new CmdletExecutor(smartContext.getConf(), userImpersonationStrategy);
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    HazelcastInstance instance = HazelcastInstanceProvider.getInstance();
    this.statusTopic = instance.getTopic(HazelcastExecutorService.STATUS_TOPIC);
    String instanceId = String.valueOf(instance.getCluster().getLocalMember().getUuid());
    ITopic<Serializable> masterMessages =
        instance.getTopic(HazelcastExecutorService.WORKER_TOPIC_PREFIX + instanceId);
    masterMessages.addMessageListener(new MasterMessageListener());
  }

  public void start() {
    int reportPeriod = smartConf.getInt(SmartConfKeys.SMART_STATUS_REPORT_PERIOD_KEY,
        SmartConfKeys.SMART_STATUS_REPORT_PERIOD_DEFAULT);
    StatusReportTask statusReportTask = new StatusReportTask(this, cmdletExecutor, smartConf);
    executorService.scheduleAtFixedRate(
        statusReportTask, 1000, reportPeriod, TimeUnit.MILLISECONDS);
  }

  public void stop() {
    executorService.shutdown();
    cmdletExecutor.shutdown();
    factory.close();
  }

  @Override
  public void report(StatusMessage status) {
    statusTopic.publish(status);
  }

  private class MasterMessageListener implements MessageListener<Serializable> {
    @Override
    public void onMessage(Message<Serializable> message) {
      Serializable msg = message.getMessageObject();
      if (msg instanceof LaunchCmdlet) {
        LaunchCmdlet launchCmdlet = (LaunchCmdlet) msg;
        try {
          cmdletExecutor.execute(factory.createCmdlet(launchCmdlet));
        } catch (ActionException e) {
          LOG.error("Failed to create cmdlet from {}", launchCmdlet, e);
          report(
              new CmdletStatusUpdate(
                  launchCmdlet.getCmdletId(), System.currentTimeMillis(), CmdletState.FAILED));
        }
      } else if (msg instanceof StopCmdlet) {
        StopCmdlet stopCmdlet = (StopCmdlet) msg;
        cmdletExecutor.stop(stopCmdlet.getCmdletId());
      }
    }
  }
}
