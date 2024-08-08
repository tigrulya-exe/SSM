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
package org.smartdata.server.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.AbstractService;
import org.smartdata.SmartConstants;
import org.smartdata.SmartContext;
import org.smartdata.hdfs.scheduler.ActionSchedulerService;
import org.smartdata.metastore.MetaStore;
import org.smartdata.metastore.StatesUpdateService;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbstractServiceFactory {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractServiceFactory.class);

  public static AbstractService createStatesUpdaterService(
      ServerContext context, MetaStore metaStore) throws IOException {
    return createNewInstance(context,
        metaStore,
        SmartConstants.SMART_STATES_UPDATE_SERVICE_IMPL,
        true)
        .map(service -> (StatesUpdateService) service)
        .orElseThrow(() -> new IOException("Failed to create update states service instance"));
  }

  public static List<ActionSchedulerService> createActionSchedulerServices(
      ServerContext context, MetaStore metaStore, boolean allMustSuccess) throws IOException {
    List<ActionSchedulerService> services = new ArrayList<>();
    String[] serviceNames = getActionSchedulerNames();
    for (String name : serviceNames) {
      createNewInstance(context,
          metaStore,
          name,
          allMustSuccess).ifPresent(service -> services.add((ActionSchedulerService) service));
    }
    return services;
  }

  public static String[] getActionSchedulerNames() {
    return SmartConstants.SMART_ACTION_SCHEDULER_SERVICE_IMPL.trim().split("\\s*,\\s*");
  }

  private static Optional<Object> createNewInstance(ServerContext context,
                                                    MetaStore metaStore,
                                                    String serviceName,
                                                    boolean allMustSuccess)
      throws IOException {
    try {
      Class<?> clazz = Class.forName(serviceName);
      Constructor<?> c = clazz.getConstructor(SmartContext.class, MetaStore.class);
      return Optional.of(c.newInstance(context, metaStore));
    } catch (ClassNotFoundException | IllegalAccessException
             | InstantiationException | NoSuchMethodException
             | InvocationTargetException | NullPointerException e) {
      if (allMustSuccess) {
        throw new IOException(e);
      } else {
        LOG.warn("Error while create action scheduler service '" + serviceName + "'.", e);
        return Optional.empty();
      }
    }
  }
}
