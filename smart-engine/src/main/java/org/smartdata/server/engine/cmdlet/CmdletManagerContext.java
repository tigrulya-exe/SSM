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
package org.smartdata.server.engine.cmdlet;

import com.google.common.collect.ListMultimap;
import java.util.List;
import org.smartdata.conf.SmartConf;
import org.smartdata.metastore.MetaStore;
import org.smartdata.model.action.ActionScheduler;
import org.smartdata.server.engine.ServerContext;

public class CmdletManagerContext extends ServerContext {
  private final InMemoryRegistry inMemoryRegistry;
  private final ListMultimap<String, ActionScheduler> schedulers;

  public CmdletManagerContext(SmartConf conf, MetaStore metaStore, InMemoryRegistry inMemoryRegistry,
                              ListMultimap<String, ActionScheduler> schedulers) {
    super(conf, metaStore);
    this.inMemoryRegistry = inMemoryRegistry;
    this.schedulers = schedulers;
  }

  public InMemoryRegistry getInMemoryRegistry() {
    return inMemoryRegistry;
  }

  public List<ActionScheduler> getSchedulers(String action) {
    return schedulers.get(action);
  }
}
