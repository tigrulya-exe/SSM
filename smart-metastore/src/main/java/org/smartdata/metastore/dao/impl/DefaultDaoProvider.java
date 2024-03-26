/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.metastore.dao.impl;

import org.smartdata.metastore.DBPool;
import org.smartdata.metastore.dao.AccessCountDao;
import org.smartdata.metastore.dao.ActionDao;
import org.smartdata.metastore.dao.BackUpInfoDao;
import org.smartdata.metastore.dao.CacheFileDao;
import org.smartdata.metastore.dao.ClusterConfigDao;
import org.smartdata.metastore.dao.ClusterInfoDao;
import org.smartdata.metastore.dao.CmdletDao;
import org.smartdata.metastore.dao.CompressionFileDao;
import org.smartdata.metastore.dao.DaoProvider;
import org.smartdata.metastore.dao.DataNodeInfoDao;
import org.smartdata.metastore.dao.DataNodeStorageInfoDao;
import org.smartdata.metastore.dao.ErasureCodingPolicyDao;
import org.smartdata.metastore.dao.FileDiffDao;
import org.smartdata.metastore.dao.FileInfoDao;
import org.smartdata.metastore.dao.FileStateDao;
import org.smartdata.metastore.dao.GeneralDao;
import org.smartdata.metastore.dao.GlobalConfigDao;
import org.smartdata.metastore.dao.RuleDao;
import org.smartdata.metastore.dao.SmallFileDao;
import org.smartdata.metastore.dao.StorageDao;
import org.smartdata.metastore.dao.StorageHistoryDao;
import org.smartdata.metastore.dao.StoragePolicyDao;
import org.smartdata.metastore.dao.SystemInfoDao;
import org.smartdata.metastore.dao.UserActivityDao;
import org.smartdata.metastore.dao.UserInfoDao;
import org.smartdata.metastore.dao.WhitelistDao;
import org.smartdata.metastore.dao.XattrDao;

import javax.sql.DataSource;

public class DefaultDaoProvider implements DaoProvider {
  protected final DataSource dataSource;

  public DefaultDaoProvider(DBPool dbPool) {
    this.dataSource = dbPool.getDataSource();
  }

  @Override
  public RuleDao ruleDao() {
    return new DefaultRuleDao(dataSource);
  }

  @Override
  public CmdletDao cmdletDao() {
    return new DefaultCmdletDao(dataSource);
  }

  @Override
  public ActionDao actionDao() {
    return new DefaultActionDao(dataSource);
  }

  @Override
  public FileInfoDao fileInfoDao() {
    return new DefaultFileInfoDao(dataSource);
  }

  @Override
  public CacheFileDao cacheFileDao() {
    return new DefaultCacheFileDao(dataSource);
  }

  @Override
  public StorageDao storageDao() {
    return new DefaultStorageDao(dataSource);
  }

  @Override
  public StorageHistoryDao storageHistoryDao() {
    return new DefaultStorageHistoryDao(dataSource);
  }

  @Override
  public XattrDao xattrDao() {
    return new DefaultXattrDao(dataSource);
  }

  @Override
  public FileDiffDao fileDiffDao() {
    return new DefaultFileDiffDao(dataSource);
  }

  @Override
  public AccessCountDao accessCountDao() {
    return new DefaultAccessCountDao(dataSource);
  }

  @Override
  public ClusterConfigDao clusterConfigDao() {
    return new DefaultClusterConfigDao(dataSource);
  }

  @Override
  public GlobalConfigDao globalConfigDao() {
    return new DefaultGlobalConfigDao(dataSource);
  }

  @Override
  public DataNodeInfoDao dataNodeInfoDao() {
    return new DefaultDataNodeInfoDao(dataSource);
  }

  @Override
  public DataNodeStorageInfoDao dataNodeStorageInfoDao() {
    return new DefaultDataNodeStorageInfoDao(dataSource);
  }

  @Override
  public BackUpInfoDao backUpInfoDao() {
    return new DefaultBackUpInfoDao(dataSource);
  }

  @Override
  public ClusterInfoDao clusterInfoDao() {
    return new DefaultClusterInfoDao(dataSource);
  }

  @Override
  public SystemInfoDao systemInfoDao() {
    return new DefaultSystemInfoDao(dataSource);
  }

  @Override
  public UserInfoDao userInfoDao() {
    return new DefaultUserInfoDao(dataSource);
  }

  @Override
  public FileStateDao fileStateDao() {
    return new DefaultFileStateDao(dataSource);
  }

  @Override
  public CompressionFileDao compressionFileDao() {
    return new DefaultCompressionFileDao(dataSource);
  }

  @Override
  public GeneralDao generalDao() {
    return new DefaultGeneralDao(dataSource);
  }

  @Override
  public SmallFileDao smallFileDao() {
    return new DefaultSmallFileDao(dataSource);
  }

  @Override
  public ErasureCodingPolicyDao ecDao() {
    return new DefaultErasureCodingPolicyDao(dataSource);
  }

  @Override
  public WhitelistDao whitelistDao() {
    return new DefaultWhitelistDao(dataSource);
  }

  @Override
  public StoragePolicyDao storagePolicyDao() {
    return new DefaultStoragePolicyDao(dataSource);
  }

  @Override
  public UserActivityDao userActivityDao() {
    return new DefaultUserActivityDao(dataSource);
  }
}
