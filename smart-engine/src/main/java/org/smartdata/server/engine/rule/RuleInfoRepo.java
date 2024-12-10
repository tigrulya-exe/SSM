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
package org.smartdata.server.engine.rule;

import org.smartdata.conf.SmartConf;
import org.smartdata.exception.StateTransitionException;
import org.smartdata.metastore.MetaStore;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.dao.RuleDao;
import org.smartdata.model.RuleInfo;
import org.smartdata.model.RuleState;
import org.smartdata.model.rule.RuleExecutorPlugin;
import org.smartdata.model.rule.RuleTranslationResult;
import org.smartdata.rule.parser.SmartRuleStringParser;
import org.smartdata.rule.parser.TranslationContext;
import org.smartdata.server.engine.RuleManager;
import org.smartdata.server.engine.data.ExecutionContext;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Contains detailed info about a rule.
 */
public class RuleInfoRepo {
  private final RuleInfo ruleInfo;
  private final MetaStore metaStore;
  private final RuleDao ruleDao;
  private final SmartConf conf;
  private final List<RuleExecutorPlugin> executorPlugins;
  private RuleExecutor ruleExecutor;

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public RuleInfoRepo(RuleInfo ruleInfo,
      MetaStore metaStore,
      SmartConf conf,
      List<RuleExecutorPlugin> executorPlugins) {
    this.ruleInfo = ruleInfo;
    this.metaStore = metaStore;
    this.ruleDao = metaStore.ruleDao();
    this.executorPlugins = executorPlugins;
    this.conf = conf;
  }

  public RuleInfo getRuleInfo() {
    lockRead();
    try {
      return ruleInfo.newCopy();
    } finally {
      unlockRead();
    }
  }

  public RuleInfo getRuleInfoRef() {
    return ruleInfo;
  }

  public void disable() throws IOException {
    lockWrite();
    try {
      changeRuleState(RuleState.DISABLED);
    } finally {
      unlockWrite();
    }
  }

  public void delete() throws IOException {
    lockWrite();
    try {
      changeRuleState(RuleState.DELETED);
      try {
        ruleDao.delete(ruleInfo.getId());
      } catch (Exception exception) {
        throw new IOException(exception);
      }
    } finally {
      unlockWrite();
    }
  }

  public RuleExecutor activate(RuleManager ruleManager)
      throws IOException {
    lockWrite();
    try {
      changeRuleState(RuleState.ACTIVE);
      return doLaunchExecutor(ruleManager);
    } finally {
      unlockWrite();
    }
  }

  public RuleExecutor launchExecutor(RuleManager ruleManager)
      throws IOException {
    lockWrite();
    try {
      return doLaunchExecutor(ruleManager);
    } finally {
      unlockWrite();
    }
  }

  public boolean updateRuleInfo(RuleState rs, long lastCheckTime,
      long checkedCount, int cmdletsGen) throws IOException {
    lockWrite();
    try {
      boolean ret = true;
      changeRuleState(rs, false);
      ruleInfo.updateRuleInfo(rs, lastCheckTime, checkedCount, cmdletsGen);
      if (metaStore != null) {
        try {
          ret = metaStore.updateRuleInfo(ruleInfo.getId(),
              rs, lastCheckTime, ruleInfo.getNumChecked(), (int) ruleInfo.getNumCmdsGen());
        } catch (MetaStoreException e) {
          throw new MetaStoreException(ruleInfo.toString(), e);
        }
      }
      return ret;
    } finally {
      unlockWrite();
    }
  }

  private RuleExecutor doLaunchExecutor(RuleManager ruleManager)
      throws IOException {
    RuleState state = ruleInfo.getState();
    if (state == RuleState.ACTIVE) {
      if (ruleExecutor != null && !ruleExecutor.isExited()) {
        return null;
      }
      ExecutionContext executionCtx = new ExecutionContext(ruleInfo.getId());
      TranslationContext translationCtx = new TranslationContext(
          ruleInfo.getId(), ruleInfo.getSubmitTime());
      RuleTranslationResult translationResult = ruleExecutor != null
          ? ruleExecutor.getOriginalTranslateResult()
          : new SmartRuleStringParser(ruleInfo.getRuleText(), translationCtx, conf).translate();

      ruleExecutor = new RuleExecutor(
          ruleManager,
          executionCtx,
          translationResult,
          ruleManager.getMetaStore(),
          executorPlugins);
      for (RuleExecutorPlugin plugin : executorPlugins) {
        plugin.onNewRuleExecutor(ruleInfo, translationResult);
      }
      return ruleExecutor;
    }
    return null;
  }

  private void markWorkExit() {
    if (ruleExecutor != null) {
      ruleExecutor.setExited();
      notifyRuleExecutorExit();
    }
  }

  private void notifyRuleExecutorExit() {
    for (RuleExecutorPlugin plugin : executorPlugins) {
      plugin.onRuleExecutorExit(ruleInfo);
    }
  }

  private void changeRuleState(RuleState newState)
      throws IOException {
    changeRuleState(newState, true);
  }

  private void changeRuleState(RuleState newState,
      boolean updateDb) throws IOException {
    RuleState oldState = ruleInfo.getState();
    if (newState == null || oldState == newState) {
      return;
    }

    try {
      switch (newState) {
        case ACTIVE:
          if (oldState == RuleState.DISABLED || oldState == RuleState.NEW) {
            ruleInfo.setState(newState);
            if (updateDb && metaStore != null) {
              metaStore.updateRuleState(ruleInfo.getId(), newState);
            }
            return;
          }
          break;

        case DISABLED:
          if (oldState == RuleState.NEW) {
            // no need to save the new state to db
            return;
          }

          if (oldState == RuleState.ACTIVE) {
            ruleInfo.setState(newState);
            markWorkExit();
            if (updateDb && metaStore != null) {
              metaStore.updateRuleState(ruleInfo.getId(), newState);
            }
            return;
          }
          break;

        case DELETED:
          ruleInfo.setState(newState);
          markWorkExit();
          if (updateDb && metaStore != null) {
            metaStore.updateRuleState(ruleInfo.getId(), newState);
          }
          return;

        case FINISHED:
          if (oldState == RuleState.ACTIVE) {
            ruleInfo.setState(newState);
            if (updateDb && metaStore != null) {
              metaStore.updateRuleState(ruleInfo.getId(), newState);
            }
            return;
          }
          break;
      }
    } catch (MetaStoreException e) {
      throw new MetaStoreException(ruleInfo.toString(), e);
    }
    throw StateTransitionException.forRule(oldState, newState);
  }

  private void lockWrite() {
    lock.writeLock().lock();
  }

  private void unlockWrite() {
    lock.writeLock().unlock();
  }

  private void lockRead() {
    lock.readLock().lock();
  }

  private void unlockRead() {
    lock.readLock().unlock();
  }
}
