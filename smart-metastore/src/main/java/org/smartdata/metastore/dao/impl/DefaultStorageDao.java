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
import org.smartdata.metastore.dao.StorageDao;
import org.smartdata.model.StorageCapacity;
import org.smartdata.model.StoragePolicy;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultStorageDao extends AbstractDao implements StorageDao {
  private static final String TABLE_NAME = "storage";

  public DefaultStorageDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public Map<String, StorageCapacity> getStorageTablesItem() {
    String sql = "SELECT * FROM storage";
    List<StorageCapacity> list = jdbcTemplate.query(sql,
        new StorageCapacityRowMapper());
    Map<String, StorageCapacity> map = new HashMap<>();
    for (StorageCapacity s : list) {
      map.put(s.getType(), s);
    }
    return map;
  }

  @Override
  public Map<Integer, String> getStoragePolicyIdNameMap() throws SQLException {
    String sql = "SELECT * FROM storage_policy";
    List<StoragePolicy> list = jdbcTemplate.query(sql,
        (rs, rowNum) -> new StoragePolicy(
            rs.getByte("sid"),
            rs.getString("policy_name")));
    Map<Integer, String> map = new HashMap<>();
    for (StoragePolicy s : list) {
      map.put((int) (s.getSid()), s.getPolicyName());
    }
    return map;
  }

  @Override
  public StorageCapacity getStorageCapacity(String type) {
    String sql = "SELECT * FROM storage WHERE type = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{type},
        new StorageCapacityRowMapper());
  }

  @Override
  public String getStoragePolicyName(int sid) {
    String sql = "SELECT policy_name FROM storage_policy WHERE sid = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{sid}, String.class);
  }

  @Override
  public synchronized void insertStoragePolicyTable(StoragePolicy s) {
    String sql = "INSERT INTO storage_policy (sid, policy_name) VALUES('"
        + s.getSid() + "','" + s.getPolicyName() + "');";
    jdbcTemplate.execute(sql);
  }

  @Override
  public int updateFileStoragePolicy(String path,
                                     Integer policyId) throws SQLException {
    String sql = String.format(
        "UPDATE file SET sid = %d WHERE path = '%s';",
        policyId, path);
    return jdbcTemplate.update(sql);
  }

  @Override
  public void insertUpdateStoragesTable(final StorageCapacity[] storages)
      throws SQLException {
    if (storages.length == 0) {
      return;
    }
    final Long curr = System.currentTimeMillis();
    String sql = "REPLACE INTO storage (type, time_stamp, capacity, free) VALUES (?,?,?,?);";
    jdbcTemplate.batchUpdate(sql,
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps,
                                int i) throws SQLException {
            ps.setString(1, storages[i].getType());
            if (storages[i].getTimeStamp() == null) {
              ps.setLong(2, curr);
            } else {
              ps.setLong(2, storages[i].getTimeStamp());
            }
            ps.setLong(3, storages[i].getCapacity());
            ps.setLong(4, storages[i].getFree());
          }

          public int getBatchSize() {
            return storages.length;
          }
        });
  }

  @Override
  public int getCountOfStorageType(String type) {
    String sql = "SELECT COUNT(*) FROM storage WHERE type = ?";
    return jdbcTemplate.queryForObject(sql, Integer.class, type);
  }

  @Override
  public void deleteStorage(String storageType) {
    final String sql = "DELETE FROM storage WHERE type = ?";
    jdbcTemplate.update(sql, storageType);
  }

  @Override
  public synchronized boolean updateStoragesTable(String type, Long timeStamp,
                                                  Long capacity, Long free) throws SQLException {
    String sql = null;
    String sqlPrefix = "UPDATE storage SET";
    String sqlCapacity = (capacity != null) ? ", capacity = '"
        + capacity + "' " : null;
    String sqlFree = (free != null) ? ", free = '" + free + "' " : null;
    String sqlTimeStamp = (timeStamp != null) ? ", time_stamp = " + timeStamp + " " : null;
    String sqlSuffix = "WHERE type = '" + type + "';";
    if (capacity != null || free != null) {
      sql = sqlPrefix + sqlCapacity + sqlFree + sqlTimeStamp + sqlSuffix;
      sql = sql.replaceFirst(",", "");
    }
    return jdbcTemplate.update(sql) == 1;
  }

  @Override
  public synchronized boolean updateStoragesTable(String type,
                                                  Long capacity, Long free) throws SQLException {
    return updateStoragesTable(type, System.currentTimeMillis(), capacity, free);
  }

  private static class StorageCapacityRowMapper implements RowMapper<StorageCapacity> {
    @Override
    public StorageCapacity mapRow(ResultSet resultSet, int i)
        throws SQLException {
      return new StorageCapacity(
          resultSet.getString("type"),
          resultSet.getLong("time_stamp"),
          resultSet.getLong("capacity"),
          resultSet.getLong("free"));
    }
  }
}
