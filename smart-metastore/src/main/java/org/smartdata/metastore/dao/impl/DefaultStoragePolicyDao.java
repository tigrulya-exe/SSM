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
package org.smartdata.metastore.dao.impl;

import org.smartdata.metastore.dao.AbstractDao;
import org.smartdata.metastore.dao.StoragePolicyDao;
import org.smartdata.model.StoragePolicy;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultStoragePolicyDao extends AbstractDao implements StoragePolicyDao {
  private static final String TABLE_NAME = "storage_policy";

  private Map<Integer, String> data = null;

  public DefaultStoragePolicyDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
    this.data = getStoragePolicyFromDB();
  }

  private Map<Integer, String> getStoragePolicyFromDB() {
    String sql = "SELECT * FROM " + TABLE_NAME;
    List<StoragePolicy> list = jdbcTemplate.query(sql, new StoragePolicyRowMapper());
    Map<Integer, String> map = new HashMap<>();
    for (StoragePolicy s : list) {
      map.put((int) (s.getSid()), s.getPolicyName());
    }
    return map;
  }

  @Override
  public Map<Integer, String> getStoragePolicyIdNameMap() {
    return this.data;
  }

  @Override
  public String getStoragePolicyName(int sid) {
    return this.data.get(sid);
  }

  @Override
  public Integer getStorageSid(String policyName) {
    for (Map.Entry<Integer, String> entry : this.data.entrySet()) {
      if (entry.getValue().equals(policyName)) {
        return entry.getKey();
      }
    }
    return -1;
  }

  @Override
  public synchronized void insertStoragePolicyTable(StoragePolicy s) {
    if (!isExist(s.getPolicyName())) {
      String sql = "INSERT INTO storage_policy (sid, policy_name) VALUES('"
          + s.getSid() + "','" + s.getPolicyName() + "');";
      jdbcTemplate.execute(sql);
      this.data.put((int) (s.getSid()), s.getPolicyName());
    }
  }

  @Override
  public synchronized void deleteStoragePolicy(int sid) {
    if (isExist(sid)) {
      final String sql = "DELETE FROM " + TABLE_NAME + " WHERE sid = ?";
      jdbcTemplate.update(sql, sid);
      this.data.remove(sid);
    }
  }

  @Override
  public synchronized void deleteStoragePolicy(String policyName) {
    Integer sid = getStorageSid(policyName);
    deleteStoragePolicy(sid);
  }

  @Override
  public boolean isExist(int sid) {
    if (getStoragePolicyName(sid) != null) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isExist(String policyName) {
    return getStorageSid(policyName) != -1;
  }

  private static class StoragePolicyRowMapper implements RowMapper<StoragePolicy> {
    @Override
    public StoragePolicy mapRow(ResultSet resultSet, int i) throws SQLException {
      return new StoragePolicy(
          resultSet.getByte("sid"),
          resultSet.getString("policy_name"));
    }
  }
}
