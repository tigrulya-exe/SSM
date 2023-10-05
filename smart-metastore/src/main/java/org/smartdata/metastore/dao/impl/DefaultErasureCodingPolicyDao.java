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
import org.smartdata.metastore.dao.ErasureCodingPolicyDao;
import org.smartdata.model.ErasureCodingPolicyInfo;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultErasureCodingPolicyDao extends AbstractDao implements ErasureCodingPolicyDao {
  private static final String TABLE_NAME = "ec_policy";
  private static final String ID = "id";
  private static final String NAME = "policy_name";

  public DefaultErasureCodingPolicyDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public ErasureCodingPolicyInfo getEcPolicyById(byte id) {
    return jdbcTemplate.queryForObject
        ("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new Object[]{id},
            new EcPolicyRowMapper());
  }

  @Override
  public ErasureCodingPolicyInfo getEcPolicyByName(String policyName) {
    return jdbcTemplate.queryForObject("SELECT * FROM " + TABLE_NAME + " WHERE policy_name=?",
        new Object[]{policyName}, new EcPolicyRowMapper());
  }

  @Override
  public List<ErasureCodingPolicyInfo> getAllEcPolicies() {
    return jdbcTemplate.query("SELECT * FROM " + TABLE_NAME, new EcPolicyRowMapper());
  }

  @Override
  public void insert(ErasureCodingPolicyInfo ecPolicy) {
    insert(ecPolicy, this::toMap);
  }

  @Override
  public void insert(List<ErasureCodingPolicyInfo> ecInfos) {
    insert(ecInfos, this::toMap);
  }

  @Override
  public void deleteAll() {
    final String sql = "DELETE FROM " + TABLE_NAME;
    jdbcTemplate.execute(sql);
  }

  private Map<String, Object> toMap(ErasureCodingPolicyInfo ecPolicy) {
    Map<String, Object> map = new HashMap<>();
    map.put(ID, ecPolicy.getID());
    map.put(NAME, ecPolicy.getEcPolicyName());
    return map;
  }

  private static class EcPolicyRowMapper implements RowMapper<ErasureCodingPolicyInfo> {
    @Override
    public ErasureCodingPolicyInfo mapRow(ResultSet resultSet, int i) throws SQLException {
      ErasureCodingPolicyInfo ecPolicy = new ErasureCodingPolicyInfo(resultSet.getByte("id"),
          resultSet.getString("policy_name"));
      return ecPolicy;
    }
  }
}
