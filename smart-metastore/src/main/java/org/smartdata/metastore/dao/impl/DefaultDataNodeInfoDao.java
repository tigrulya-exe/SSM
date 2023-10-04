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
import org.smartdata.metastore.dao.DataNodeInfoDao;
import org.smartdata.model.DataNodeInfo;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDataNodeInfoDao extends AbstractDao implements DataNodeInfoDao {
  private static final String TABLE_NAME = "datanode_info";

  public DefaultDataNodeInfoDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public List<DataNodeInfo> getAll() {
    return jdbcTemplate.query("SELECT * FROM " + TABLE_NAME,
        new DataNodeInfoRowMapper());
  }

  @Override
  public List<DataNodeInfo> getByUuid(String uuid) {
    return jdbcTemplate.query(
        "SELECT * FROM " + TABLE_NAME + " WHERE uuid = ?",
        new Object[]{uuid}, new DataNodeInfoRowMapper());
  }

  @Override
  public void insert(DataNodeInfo dataNodeInfo) {
    insert(dataNodeInfo, this::toMap);
  }

  @Override
  public void insert(DataNodeInfo[] dataNodeInfos) {
    insert(dataNodeInfos, this::toMap);
  }

  @Override
  public void insert(List<DataNodeInfo> dataNodeInfos) {
    insert(dataNodeInfos, this::toMap);
  }

  @Override
  public void delete(String uuid) {
    final String sql = "DELETE FROM " + TABLE_NAME + " WHERE uuid = ?";
    jdbcTemplate.update(sql, uuid);
  }

  @Override
  public void deleteAll() {
    final String sql = "DELETE FROM " + TABLE_NAME;
    jdbcTemplate.update(sql);
  }

  private Map<String, Object> toMap(DataNodeInfo dataNodeInfo) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("uuid", dataNodeInfo.getUuid());
    parameters.put("hostname", dataNodeInfo.getHostname());
    parameters.put("rpc_address", dataNodeInfo.getRpcAddress());
    parameters.put("cache_capacity", dataNodeInfo.getCacheCapacity());
    parameters.put("cache_used", dataNodeInfo.getCacheUsed());
    parameters.put("location", dataNodeInfo.getLocation());
    return parameters;
  }

  private static class DataNodeInfoRowMapper implements RowMapper<DataNodeInfo> {

    @Override
    public DataNodeInfo mapRow(ResultSet resultSet, int i) throws SQLException {
      return DataNodeInfo.newBuilder()
          .setUuid(resultSet.getString("uuid"))
          .setHostName(resultSet.getString("hostname"))
          .setRpcAddress(resultSet.getString("rpc_address"))
          .setCacheCapacity(resultSet.getLong("cache_capacity"))
          .setCacheUsed(resultSet.getLong("cache_used"))
          .setLocation(resultSet.getString("location"))
          .build();
    }
  }
}
