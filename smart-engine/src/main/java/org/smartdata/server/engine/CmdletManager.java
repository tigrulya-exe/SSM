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

package org.smartdata.server.engine;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.AbstractService;
import org.smartdata.action.ActionException;
import org.smartdata.cmdlet.parser.CmdletParser;
import org.smartdata.cmdlet.parser.ParsedCmdlet;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.exception.QueueFullException;
import org.smartdata.hdfs.scheduler.ActionSchedulerService;
import org.smartdata.metastore.MetaStore;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.model.ActionInfo;
import org.smartdata.model.CmdletDescriptor;
import org.smartdata.model.CmdletInfo;
import org.smartdata.model.CmdletState;
import org.smartdata.model.LaunchAction;
import org.smartdata.model.PathChecker;
import org.smartdata.model.WhitelistHelper;
import org.smartdata.model.action.ActionScheduler;
import org.smartdata.model.action.ScheduleResult;
import org.smartdata.protocol.message.ActionStatus;
import org.smartdata.protocol.message.ActionStatusFactory;
import org.smartdata.protocol.message.CmdletStatus;
import org.smartdata.protocol.message.CmdletStatusUpdate;
import org.smartdata.protocol.message.LaunchCmdlet;
import org.smartdata.protocol.message.StatusMessage;
import org.smartdata.protocol.message.StatusReport;
import org.smartdata.server.cluster.ActiveServerNodeCmdletMetrics;
import org.smartdata.server.cluster.NodeCmdletMetrics;
import org.smartdata.server.engine.action.ActionInfoHandler;
import org.smartdata.server.engine.action.ActionStatusUpdateListener;
import org.smartdata.server.engine.audit.UserCmdletLifecycleListener;
import org.smartdata.server.engine.cmdlet.CmdletDispatcher;
import org.smartdata.server.engine.cmdlet.CmdletExecutorService;
import org.smartdata.server.engine.cmdlet.CmdletInfoHandler;
import org.smartdata.server.engine.cmdlet.CmdletManagerContext;
import org.smartdata.server.engine.cmdlet.DeleteTerminatedCmdletsTask;
import org.smartdata.server.engine.cmdlet.DetectTimeoutActionsTask;
import org.smartdata.server.engine.cmdlet.InMemoryRegistry;
import org.smartdata.server.engine.cmdlet.TaskTracker;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.smartdata.model.UserActivityResult.FAILURE;
import static org.smartdata.model.UserActivityResult.SUCCESS;
import static org.smartdata.model.action.ScheduleResult.RETRY;
import static org.smartdata.model.action.ScheduleResult.isSuccessful;

/**
 * When a Cmdlet is submitted, it's string descriptor will be stored into set submittedCmdlets
 * to avoid duplicated Cmdlet, then enqueue into pendingCmdlet. When the Cmdlet is scheduled it
 * will be remove out of the queue and marked in the runningCmdlets.
 *
 * <p>The map idToCmdlets stores all the recent CmdletInfos, including pending and running Cmdlets.
 * After the Cmdlet is finished or cancelled or failed, it's status will be flush to DB.
 */
public class CmdletManager extends AbstractService implements ActionStatusUpdateListener {
  private static final Logger LOG = LoggerFactory.getLogger(CmdletManager.class);

  private final ScheduledExecutorService executorService;
  private final MetaStore metaStore;
  private final int maxNumPendingCmdlets;
  private final List<Long> pendingCmdlets;
  private final List<Long> schedulingCmdlets;
  private final Queue<Long> scheduledCmdlets;
  private final Map<Long, LaunchCmdlet> idToLaunchCmdlets;
  private final List<Long> runningCmdlets;
  // Track a CmdletDescriptor from the submission to
  // the finish.
  private final TaskTracker tracker;
  private final DeleteTerminatedCmdletsTask cmdletPurgeTask;
  private final DetectTimeoutActionsTask detectTimeoutActionsTask;
  private final InMemoryRegistry inMemoryRegistry;
  private final ActionInfoHandler actionInfoHandler;
  private final CmdletInfoHandler cmdletInfoHandler;
  private final CmdletParser cmdletParser;
  private final ListMultimap<String, ActionScheduler> schedulers;
  private final UserCmdletLifecycleListener lifecycleListener;
  private final PathChecker pathChecker;
  private List<ActionSchedulerService> schedulerServices;
  private CmdletDispatcher dispatcher;

  public CmdletManager(
      ServerContext context,
      UserCmdletLifecycleListener lifecycleListener
  ) throws IOException {
    super(context);

    this.metaStore = context.getMetaStore();
    this.executorService = Executors.newScheduledThreadPool(4);
    this.runningCmdlets = new ArrayList<>();
    this.pendingCmdlets = new LinkedList<>();
    this.schedulingCmdlets = new LinkedList<>();
    this.scheduledCmdlets = new LinkedBlockingQueue<>();
    this.idToLaunchCmdlets = new ConcurrentHashMap<>();
    this.schedulers = ArrayListMultimap.create();
    this.schedulerServices = new ArrayList<>();

    this.tracker = new TaskTracker();
    this.dispatcher = new CmdletDispatcher(context, this, scheduledCmdlets,
        idToLaunchCmdlets, runningCmdlets, schedulers);
    this.pathChecker = new PathChecker(context.getConf());
    this.maxNumPendingCmdlets = context.getConf()
        .getInt(SmartConfKeys.SMART_CMDLET_MAX_NUM_PENDING_KEY,
            SmartConfKeys.SMART_CMDLET_MAX_NUM_PENDING_DEFAULT);
    this.lifecycleListener = lifecycleListener;

    this.cmdletPurgeTask = new DeleteTerminatedCmdletsTask(getContext().getConf(), metaStore);
    this.inMemoryRegistry = new InMemoryRegistry(context, tracker, executorService);

    CmdletManagerContext cmdletManagerContext = new CmdletManagerContext(
        getContext().getConf(), metaStore, inMemoryRegistry, schedulers);
    this.detectTimeoutActionsTask =
        new DetectTimeoutActionsTask(cmdletManagerContext, this, idToLaunchCmdlets.keySet());
    this.actionInfoHandler = new ActionInfoHandler(cmdletManagerContext);
    this.cmdletInfoHandler = new CmdletInfoHandler(cmdletManagerContext, actionInfoHandler);
    this.cmdletParser = new CmdletParser();
  }

  @VisibleForTesting
  void setDispatcher(CmdletDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  public ActionInfoHandler getActionInfoHandler() {
    return actionInfoHandler;
  }

  public CmdletInfoHandler getCmdletInfoHandler() {
    return cmdletInfoHandler;
  }

  @Override
  public void init() throws IOException {
    LOG.info("Initializing ...");
    try {
      cmdletPurgeTask.init();
      cmdletInfoHandler.init();
      actionInfoHandler.init();

      schedulerServices = AbstractServiceFactory.createActionSchedulerServices(
          (ServerContext) getContext(), metaStore, false);

      for (ActionSchedulerService actionSchedulerService : schedulerServices) {
        actionSchedulerService.init();
        List<String> actions = actionSchedulerService.getSupportedActions();
        for (String action : actions) {
          schedulers.put(action, actionSchedulerService);
        }
      }
      loadCmdletsFromDb();
      LOG.info("Initialized.");
    } catch (MetaStoreException e) {
      LOG.error("DB Connection error! Failed to get Max CmdletId/ActionId!", e);
      throw new IOException(e);
    } catch (IOException e) {
      throw e;
    } catch (Exception t) {
      throw new IOException(t);
    }
  }

  @VisibleForTesting
  public List<ActionScheduler> getSchedulers(String actionName) {
    return schedulers.get(actionName);
  }

  private void loadCmdletsFromDb() throws IOException {
    LOG.info("reloading the dispatched and pending cmdlets in DB.");
    try {
      for (CmdletInfo cmdletInfo : metaStore.getCmdlets(CmdletState.DISPATCHED)) {
        recoverCmdletInfo(cmdletInfo,
            actionInfos -> recoverDispatchedActionInfos(cmdletInfo, actionInfos));
      }

      for (CmdletInfo cmdletInfo : metaStore.getCmdlets(CmdletState.PENDING)) {
        recoverCmdletInfo(cmdletInfo, actionInfos -> {
        });
      }
    } catch (MetaStoreException e) {
      LOG.error("DB connection error occurs when ssm is reloading cmdlets!");
    } catch (ParseException pe) {
      LOG.error("Failed to parse cmdlet string for tracking task", pe);
    }
  }

  private void recoverDispatchedActionInfos(CmdletInfo cmdletInfo, List<ActionInfo> actionInfos) {
    for (ActionInfo actionInfo : actionInfos) {
      actionInfo.setCreateTime(cmdletInfo.getGenerateTime());
      actionInfo.setFinishTime(System.currentTimeMillis());
      // Recover scheduler status according to dispatched action.
      onActionInfoRecover(actionInfo);
    }
  }

  private void recoverCmdletInfo(
      CmdletInfo cmdletInfo,
      Consumer<List<ActionInfo>> actionInfosHandler) throws IOException, ParseException {
    CmdletDescriptor cmdletDescriptor =
        buildCmdletDescriptor(cmdletInfo.getParameters());
    cmdletDescriptor.setRuleId(cmdletInfo.getRid());
    tracker.track(cmdletInfo.getCid(), cmdletDescriptor);

    LOG.debug("Reload cmdlet: {}", cmdletInfo);
    List<ActionInfo> actionInfos = actionInfoHandler.getActions(cmdletInfo.getAids());
    actionInfosHandler.accept(actionInfos);
    syncCmdAction(cmdletInfo, actionInfos);
  }

  /**
   * Only recover scheduler status according to dispatched task.
   */
  private void onActionInfoRecover(ActionInfo actionInfo) {
    for (ActionScheduler scheduler : schedulers.get(actionInfo.getActionName())) {
      scheduler.recover(actionInfo);
    }
  }

  /**
   * Let Scheduler check actioninfo onsubmit and add them to cmdletinfo.
   */
  private void checkActionsOnSubmit(CmdletInfo cmdletInfo,
                                    List<ActionInfo> actionInfos) throws IOException {
    for (ActionInfo actionInfo : actionInfos) {
      cmdletInfo.addAction(actionInfo.getActionId());
    }
    for (ActionInfo actionInfo : actionInfos) {
      for (ActionScheduler p : schedulers.get(actionInfo.getActionName())) {
        if (!p.onSubmit(cmdletInfo, actionInfo)) {
          throw new IOException("Action rejected by scheduler: " + actionInfo);
        }
      }
    }
  }

  @Override
  public void start() throws IOException {
    LOG.info("Starting ...");
    executorService.scheduleAtFixedRate(cmdletPurgeTask, 10, 5000, TimeUnit.MILLISECONDS);
    executorService.scheduleAtFixedRate(new ScheduleTask(), 100, 50, TimeUnit.MILLISECONDS);
    executorService.scheduleAtFixedRate(detectTimeoutActionsTask, 1000, 5000,
        TimeUnit.MILLISECONDS);

    inMemoryRegistry.start();

    for (ActionSchedulerService scheduler : schedulerServices) {
      scheduler.start();
    }
    dispatcher.start();
    LOG.info("Started.");
  }

  @Override
  public void stop() throws IOException {
    LOG.info("Stopping ...");
    dispatcher.stop();

    for (ActionSchedulerService scheduler:  schedulerServices) {
      try {
        scheduler.stop();
      } catch (Exception exception) {
        LOG.error("Error stopping scheduler {}", scheduler.getClass(), exception);
      }
    }

    executorService.shutdown();
    inMemoryRegistry.stop();

    dispatcher.shutDownExcutorServices();
    LOG.info("Stopped.");
  }

  /**
   * Register agentExecutorService & hazelcastExecutorService.
   */
  public void registerExecutorService(CmdletExecutorService executorService) {
    dispatcher.registerExecutorService(executorService);
  }

  public long submitCmdlet(String cmdlet) throws IOException {
    try {
      long cmdletId = submitCmdletInternal(cmdlet);
      lifecycleListener.onCmdletAdded(cmdletId);
      return cmdletId;
    } catch (Exception exception) {
      lifecycleListener.onCmdletAddFailure(cmdlet);
      throw exception;
    }
  }

  private long submitCmdletInternal(String cmdlet) throws IOException {
    LOG.debug("Received Cmdlet -> [ {} ]", cmdlet);
    try {
      if (StringUtils.isBlank(cmdlet)) {
        throw new IOException("Cannot submit an empty action!");
      }
      CmdletDescriptor cmdletDescriptor = buildCmdletDescriptor(cmdlet);
      return submitCmdlet(cmdletDescriptor);
    } catch (ParseException e) {
      LOG.error("Wrong format for cmdlet '{}'", cmdlet, e);
      throw new IOException(e);
    }
  }

  private CmdletDescriptor buildCmdletDescriptor(String cmdlet) throws ParseException {
    ParsedCmdlet parsedCmdlet = cmdletParser.parse(cmdlet);
    return new CmdletDescriptor(parsedCmdlet);
  }

  private void validatePendingCmdletsCount() throws QueueFullException {
    if (maxNumPendingCmdlets <= pendingCmdlets.size() + schedulingCmdlets.size()) {
      throw new QueueFullException("Pending cmdlets exceeds value specified by key '"
          + SmartConfKeys.SMART_CMDLET_MAX_NUM_PENDING_KEY + "' = " + maxNumPendingCmdlets);
    }
  }

  public long submitCmdlet(CmdletDescriptor cmdletDescriptor) throws IOException {
    // To avoid repeatedly submitting task. If tracker contains one CmdletDescriptor
    // with the same rule id and cmdlet string, return -1.
    if (tracker.contains(cmdletDescriptor)) {
      LOG.warn("Refuse to repeatedly submit cmdlet '{}'", cmdletDescriptor);
      return -1;
    }
    validatePendingCmdletsCount();

    CmdletInfo cmdletInfo = cmdletInfoHandler
        .createCmdletInfo(cmdletDescriptor);
    List<ActionInfo> actionInfos = actionInfoHandler
        .createActionInfos(cmdletDescriptor, cmdletInfo);

    // Check if action path is in whitelist
    WhitelistHelper.validateCmdletPathCovered(cmdletDescriptor, pathChecker);

    // Let Scheduler check actioninfo onsubmit and add them to cmdletinfo
    checkActionsOnSubmit(cmdletInfo, actionInfos);
    // Insert cmdletinfo and actionInfos to metastore and cache.
    syncCmdAction(cmdletInfo, actionInfos);
    // Track in the submission portal. For cmdlets recovered from DB
    // (see #recover), they will be not be tracked.
    tracker.track(cmdletInfo.getCid(), cmdletDescriptor);
    return cmdletInfo.getCid();
  }

  /**
   * Insert cmdletInfo and actions to metastore and cache.
   */
  private void syncCmdAction(CmdletInfo cmdletInfo,
                             List<ActionInfo> actionInfos) {
    LOG.debug("Cache cmdlet {}", cmdletInfo);
    actionInfos.forEach(actionInfoHandler::store);
    cmdletInfoHandler.storeUnfinished(cmdletInfo);

    if (cmdletInfo.getState() == CmdletState.PENDING) {
      synchronized (pendingCmdlets) {
        pendingCmdlets.add(cmdletInfo.getCid());
      }
    } else if (cmdletInfo.getState() == CmdletState.DISPATCHED) {
      runningCmdlets.add(cmdletInfo.getCid());
      LaunchCmdlet launchCmdlet = cmdletInfoHandler.createLaunchCmdlet(cmdletInfo);
      idToLaunchCmdlets.put(cmdletInfo.getCid(), launchCmdlet);
    }
  }

  private boolean shouldStopSchedule() {
    int left = dispatcher.getTotalSlotsLeft();
    int total = dispatcher.getTotalSlots();
    return scheduledCmdlets.size() >= left + total * 0.2;
  }

  private int getNumPendingScheduleCmdlets() {
    return pendingCmdlets.size() + schedulingCmdlets.size();
  }

  public void updateNodeCmdletMetrics(ActiveServerNodeCmdletMetrics metrics) {
    metrics.setMaxPendingSchedule(maxNumPendingCmdlets);
    metrics.setNumPendingSchedule(getNumPendingScheduleCmdlets());
  }

  public Collection<NodeCmdletMetrics> getAllNodeCmdletMetrics() {
    return dispatcher.getNodeCmdletMetrics();
  }

  private void scheduleCmdlets() {
    synchronized (pendingCmdlets) {
      if (!pendingCmdlets.isEmpty()) {
        schedulingCmdlets.addAll(pendingCmdlets);
        pendingCmdlets.clear();
      }
    }

    Iterator<Long> cmdletIdsIter = schedulingCmdlets.iterator();
    while (cmdletIdsIter.hasNext() && !shouldStopSchedule()) {
      CmdletInfo cmdlet = cmdletInfoHandler.getUnfinishedCmdlet(cmdletIdsIter.next());
      if (cmdlet == null) {
        cmdletIdsIter.remove();
        continue;
      }

      synchronized (cmdlet) {
        LaunchCmdlet launchCmdlet = cmdletInfoHandler.createLaunchCmdlet(cmdlet);
        Optional<ScheduleResult> scheduleResult = scheduleCmdlet(cmdlet, launchCmdlet);

        if (scheduleResult.filter(RETRY::equals).isPresent()) {
          continue;
        }

        cmdletIdsIter.remove();
        scheduleResult
            .ifPresent(result -> handleScheduleResult(result, cmdlet, launchCmdlet));
      }
    }
  }

  private Optional<ScheduleResult> scheduleCmdlet(CmdletInfo cmdlet, LaunchCmdlet launchCmdlet) {
    if (Objects.requireNonNull(cmdlet.getState()) == CmdletState.PENDING) {
      try {
        return Optional.of(scheduleCmdletActions(cmdlet, launchCmdlet));
      } catch (Exception exception) {
        LOG.error("Error scheduling {}", cmdlet, exception);
        return Optional.of(ScheduleResult.FAIL);
      }
    }
    return Optional.empty();
  }

  private void handleScheduleResult(
      ScheduleResult result,
      CmdletInfo cmdlet,
      LaunchCmdlet launchCmdlet) {
    try {
      switch (result) {
        case SUCCESS:
          cmdlet.updateState(CmdletState.SCHEDULED);
          idToLaunchCmdlets.put(cmdlet.getCid(), launchCmdlet);
          scheduledCmdlets.add(cmdlet.getCid());
          break;
        case FAIL:
          cmdlet.updateState(CmdletState.CANCELLED);
          cmdletPurgeTask.onCmdletFinished();
          cmdletInfoHandler.onCmdletFinished(cmdlet, false);
          onCmdletStatusUpdate(statusFromCmdletInfo(cmdlet));
          break;
        case SUCCESS_NO_EXECUTION:
          cmdlet.updateState(CmdletState.DONE);
          cmdletPurgeTask.onCmdletFinished();
          cmdletInfoHandler.onCmdletFinished(cmdlet, true);
          onCmdletStatusUpdate(statusFromCmdletInfo(cmdlet));
      }
    } catch (Exception exception) {
      LOG.error("Error handling scheduling result for {}", cmdlet, exception);
    }
  }

  private CmdletStatus statusFromCmdletInfo(CmdletInfo cmdletInfo) {
    return new CmdletStatus(
        cmdletInfo.getCid(), cmdletInfo.getStateChangedTime(), cmdletInfo.getState());
  }

  private ScheduleResult scheduleCmdletActions(CmdletInfo info, LaunchCmdlet launchCmdlet) {
    List<Long> actionIds = info.getAids();
    int actionIdx;
    int schIdx = 0;
    boolean skipped = false;
    ScheduleResult scheduleResult = ScheduleResult.SUCCESS_NO_EXECUTION;

    actionsCycle:
    for (actionIdx = 0; actionIdx < actionIds.size(); actionIdx++) {
      ActionInfo actionInfo = actionInfoHandler.getUnfinishedAction(actionIds.get(actionIdx));
      LaunchAction launchAction = launchCmdlet.getLaunchActions().get(actionIdx);

      List<ActionScheduler> actionSchedulers = schedulers.get(actionInfo.getActionName());
      if (CollectionUtils.isEmpty(actionSchedulers)) {
        skipped = true;
        continue;
      }

      for (schIdx = 0; schIdx < actionSchedulers.size(); schIdx++) {
        ActionScheduler scheduler = actionSchedulers.get(schIdx);
        try {
          scheduleResult = scheduler.onSchedule(info, actionInfo, launchCmdlet, launchAction);
        } catch (Exception exception) {
          actionInfo.appendLogLine("OnSchedule exception: " + exception);
          scheduleResult = ScheduleResult.FAIL;
        }

        if (!isSuccessful(scheduleResult)) {
          break actionsCycle;
        }
      }
    }

    if (isSuccessful(scheduleResult)) {
      actionIdx--;
      schIdx--;
      // todo check do we need it
      if (skipped) {
        scheduleResult = ScheduleResult.SUCCESS;
      }
    }
    postScheduleCmdletActions(info, actionIds, scheduleResult, actionIdx, schIdx);
    return scheduleResult;
  }

  private void postScheduleCmdletActions(
      CmdletInfo cmdletInfo,
      List<Long> actions,
      ScheduleResult result,
      int lastActionIdx,
      int lastSchedulerIdx) {
    for (int actionIdx = lastActionIdx; actionIdx >= 0; actionIdx--) {
      ActionInfo info = actionInfoHandler.getUnfinishedAction(actions.get(actionIdx));
      List<ActionScheduler> actionSchedulers = schedulers.get(info.getActionName());
      if (CollectionUtils.isEmpty(actionSchedulers)) {
        continue;
      }
      if (lastSchedulerIdx < 0) {
        lastSchedulerIdx = actionSchedulers.size() - 1;
      }

      for (int schedulerIdx = lastSchedulerIdx; schedulerIdx >= 0; schedulerIdx--) {
        try {
          actionSchedulers.get(schedulerIdx).postSchedule(cmdletInfo, info, result);
        } catch (Exception exception) {
          info.appendLogLine("PostSchedule exception: " + exception);
        }
      }

      lastSchedulerIdx = -1;
    }
  }


  public void disableCmdlet(long cmdletId) throws IOException {
    try {
      disableCmdletInternal(cmdletId);
      lifecycleListener.onCmdletStop(cmdletId, SUCCESS);
    } catch (Exception exception) {
      lifecycleListener.onCmdletStop(cmdletId, FAILURE);
      throw exception;
    }
  }

  public void disableCmdletInternal(long cmdletId) throws IOException {
    CmdletInfo info = cmdletInfoHandler.getUnfinishedCmdlet(cmdletId);
    if (info == null) {
      return;
    }
    onCmdletStatusUpdate(
        new CmdletStatus(info.getCid(), System.currentTimeMillis(), CmdletState.DISABLED));

    synchronized (pendingCmdlets) {
      pendingCmdlets.remove(cmdletId);
    }

    schedulingCmdlets.remove(cmdletId);

    scheduledCmdlets.remove(cmdletId);

    // Wait status update from status reporter, so need to update to MetaStore
    if (runningCmdlets.contains(cmdletId)) {
      dispatcher.stopCmdlet(cmdletId);
    }
  }

  /**
   * Drop all unfinished cmdlets.
   */
  public void deletePendingRuleCmdlets(long ruleId) throws IOException {
    List<Long> cmdletIds = cmdletInfoHandler.deleteUnfinishedCmdletsByRule(ruleId);
    disableCmdlets(cmdletIds);
  }

  //Todo: optimize this function.
  private void cmdletFinished(CmdletInfo cmdletInfo) {
    cmdletPurgeTask.onCmdletFinished();

    dispatcher.onCmdletFinished(cmdletInfo.getCid());
    runningCmdlets.remove(cmdletInfo.getCid());
    idToLaunchCmdlets.remove(cmdletInfo.getCid());

    cmdletInfoHandler.store(cmdletInfo);
  }

  public void deleteCmdlet(long cmdletId) throws IOException {
    try {
      disableCmdlet(cmdletId);
      cmdletInfoHandler.deleteCmdlet(cmdletId);

      lifecycleListener.onCmdletDelete(cmdletId, SUCCESS);
    } catch (Exception exception) {
      lifecycleListener.onCmdletDelete(cmdletId, FAILURE);
      throw exception;
    }
  }

  private void disableCmdlets(List<Long> cmdletIds) throws IOException {
    for (long cmdletId : cmdletIds) {
      disableCmdletInternal(cmdletId);
    }
  }

  public void updateCmdletExecHost(long cmdletId, String host) throws IOException {
    cmdletInfoHandler.updateCmdletExecHost(cmdletId, host);
  }

  /**
   * Delete all cmdlets related with rid.
   */
  public void deleteCmdletByRule(long rid) throws IOException {
    List<Long> cmdletIds = cmdletInfoHandler.deleteCmdletsByRule(rid);
    disableCmdlets(cmdletIds);
  }

  public void updateStatus(StatusMessage status) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Got status update: " + status);
    }
    try {
      if (status instanceof CmdletStatusUpdate) {
        CmdletStatusUpdate statusUpdate = (CmdletStatusUpdate) status;
        onCmdletStatusUpdate(statusUpdate.getCmdletStatus());
      } else if (status instanceof StatusReport) {
        onStatusReport((StatusReport) status);
      }
    } catch (IOException e) {
      LOG.error(String.format("Update status %s failed with %s", status, e));
    } catch (ActionException e) {
      LOG.error("Action Status error", e);
    }
  }

  private void onStatusReport(StatusReport report) throws IOException, ActionException {
    List<ActionStatus> actionStatusList = report.getActionStatuses();
    if (actionStatusList == null) {
      return;
    }
    for (ActionStatus actionStatus : actionStatusList) {
      onStatusUpdate(actionStatus);
    }
  }

  @Override
  public void onStatusUpdate(ActionStatus actionStatus) throws IOException {
    onActionStatusUpdate(actionStatus);
    ActionInfo actionInfo = actionInfoHandler.getUnfinishedAction(actionStatus.getActionId());
    inferCmdletStatus(actionInfo);
  }

  public void onCmdletStatusUpdate(CmdletStatus status) throws IOException {
    CmdletInfo cmdletInfo = cmdletInfoHandler
        .updateCmdletStatus(status.getCmdletId(), status);
    if (cmdletInfo == null) {
      return;
    }

    CmdletState state = status.getCurrentState();
    if (CmdletState.isTerminalState(state)) {
      cmdletFinished(cmdletInfo);
    } else if (state == CmdletState.DISPATCHED) {
      cmdletInfoHandler.store(cmdletInfo);
    }
  }

  public void onActionStatusUpdate(ActionStatus status)
      throws IOException {
    if (status == null) {
      return;
    }
    CmdletInfo cmdletInfo =
        cmdletInfoHandler.getUnfinishedCmdlet(status.getCmdletId());
    ActionInfo actionInfo =
        actionInfoHandler.updateActionStatus(status.getActionId(), status);

    if (actionInfo != null && status.isFinished()) {
      for (ActionScheduler scheduler : schedulers.get(actionInfo.getActionName())) {
        scheduler.onActionFinished(cmdletInfo, actionInfo);
      }
    }
  }

  private void inferCmdletStatus(ActionInfo actionInfo) throws IOException {
    if (actionInfo == null || !actionInfo.isFinished()) {
      return;
    }

    long cmdletId = actionInfo.getCmdletId();

    CmdletInfo cmdletInfo = cmdletInfoHandler.getUnfinishedCmdlet(cmdletId);
    List<Long> actionIds = cmdletInfo.getAids();
    int actionIndex = actionIds.indexOf(actionInfo.getActionId());

    if (!actionInfo.isSuccessful()) {
      for (int i = actionIndex + 1; i < actionIds.size(); i++) {
        // Use current action's finish time to set start/finish time for
        // subsequent action(s).
        ActionStatus actionStatus = ActionStatusFactory.createSkipActionStatus(
            cmdletId, i == actionIds.size() - 1, actionIds.get(i),
            actionInfo.getFinishTime(), actionInfo.getFinishTime());
        onActionStatusUpdate(actionStatus);
      }
      CmdletStatus cmdletStatus =
          new CmdletStatus(cmdletId, actionInfo.getFinishTime(), CmdletState.FAILED);
      onCmdletStatusUpdate(cmdletStatus);
    } else if (actionIndex == actionIds.size() - 1) {
      CmdletStatus cmdletStatus =
          new CmdletStatus(cmdletId, actionInfo.getFinishTime(), CmdletState.DONE);
      onCmdletStatusUpdate(cmdletStatus);
    }
  }

  private class ScheduleTask implements Runnable {
    @Override
    public void run() {
      try {
        scheduleCmdlets();
      } catch (Exception exception) {
        // no meaningful info, ignore
      }
    }
  }
}
