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
import org.smartdata.metastore.dao.StorageHistoryDao;
import org.smartdata.model.StorageCapacity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DefaultStorageHistoryDao extends AbstractDao implements StorageHistoryDao {
  private static final String TABLE_NAME = "storage_hist";

  public DefaultStorageHistoryDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public void insertStorageHistTable(final StorageCapacity[] storages, final long interval)
      throws SQLException {
    if (storages.length == 0) {
      return;
    }
    final Long curr = System.currentTimeMillis();
    String sql = "INSERT INTO storage_hist (type, time_stamp, capacity, free) VALUES (?,?,?,?);";
    jdbcTemplate.batchUpdate(sql,
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps,
                                int i) throws SQLException {
            ps.setString(1, storages[i].getType() + "-" + interval);
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
  public List<StorageCapacity> getStorageHistoryData(String type, long interval,
                                                     long startTime, long endTime) {
    String sql = "SELECT * FROM storage_hist WHERE type = ? AND "
        + "time_stamp BETWEEN ? AND ?";
    List<StorageCapacity> data = jdbcTemplate.query(sql,
        new Object[]{type + "-" + interval, startTime, endTime},
        new StorageHistoryRowMapper());
    for (StorageCapacity sc : data) {
      sc.setType(type);
    }
    return data;
  }

  @Override
  public int getNumberOfStorageHistoryData(String type, long interval) {
    String sql = "SELECT COUNT(*) FROM storage_hist WHERE type = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{type + "-" + interval}, Integer.class);
  }

  @Override
  public void deleteOldRecords(String type, long interval, long beforTimeStamp) {
    String sql = "DELETE FROM storage_hist WHERE type = ? AND time_stamp <= ?";
    jdbcTemplate.update(sql, type + "-" + interval, beforTimeStamp);
  }

  private static class StorageHistoryRowMapper implements RowMapper<StorageCapacity> {
    @Override
    public StorageCapacity mapRow(ResultSet resultSet, int i) throws SQLException {
      return new StorageCapacity(resultSet.getString("type"),
          resultSet.getLong("time_stamp"),
          resultSet.getLong("capacity"), resultSet.getLong("free"));
    }
  }
}
