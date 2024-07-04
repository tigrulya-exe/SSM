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
package org.smartdata.metastore.transaction;

import org.smartdata.metastore.MetaStoreException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionRunner {
  private final TransactionTemplate transactionTemplate;

  public TransactionRunner(PlatformTransactionManager transactionManager) {
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  public void setIsolationLevel(Isolation isolation) {
    transactionTemplate.setIsolationLevel(isolation.value());
  }

  public void setPropagationBehavior(Propagation propagation) {
    transactionTemplate.setPropagationBehavior(propagation.value());
  }

  public PlatformTransactionManager getTransactionManager() {
    return transactionTemplate.getTransactionManager();
  }

  public <V> V inTransaction(TransactionQuery<V> query) throws
      MetaStoreException {
    try {
      return transactionTemplate.execute(query.toCallback());
    } catch (WrappedTransactionException exception) {
      throw new MetaStoreException(exception.getCause());
    }
  }

  public void inTransaction(TransactionStatement statement) throws MetaStoreException {
    try {
      transactionTemplate.executeWithoutResult(statement.toCallback());
    } catch (WrappedTransactionException exception) {
      throw new MetaStoreException(exception.getCause());
    }
  }
}
