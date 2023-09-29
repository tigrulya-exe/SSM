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
import org.smartdata.metastore.dao.UserInfoDao;
import org.smartdata.model.UserInfo;
import org.smartdata.utils.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultUserInfoDao extends AbstractDao implements UserInfoDao {
  private static final String TABLE_NAME = "user_info";

  public DefaultUserInfoDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public List<UserInfo> getAll() {
    return jdbcTemplate.query("SELECT * FROM " + TABLE_NAME, new UserInfoRowMapper());
  }

  @Override
  public boolean containsUserName(String name) {
    return !list(name).isEmpty();
  }

  private List<UserInfo> list(String name) {
    return new JdbcTemplate(dataSource)
        .query(
            "SELECT * FROM " + TABLE_NAME + " WHERE user_name = ?",
            new Object[]{name},
            new UserInfoRowMapper());
  }

  @Override
  public UserInfo getByUserName(String name) {
    List<UserInfo> infos = list(name);
    return infos.isEmpty() ? null : infos.get(0);
  }

  @Override
  public void delete(String name) {
    final String sql = "DELETE FROM " + TABLE_NAME + " WHERE user_name = ?";
    jdbcTemplate.update(sql, name);
  }

  @Override
  public void insert(UserInfo userInfo) {
    UserInfo userWithCachedPassword = new UserInfo(
        userInfo.getUserName(),
        StringUtil.toSHA512String(userInfo.getUserPassword()));
    insert(userWithCachedPassword, this::toMap);
  }

  @Override
  public boolean authentic(UserInfo userInfo) {
    UserInfo origin = getByUserName(userInfo.getUserName());
    return origin.equals(userInfo);
  }

  @Override
  public int newPassword(UserInfo userInfo) {
    String sql = "UPDATE " + TABLE_NAME + " SET user_password = ? WHERE user_name = ?";
    return jdbcTemplate.update(sql, StringUtil.toSHA512String(userInfo.getUserPassword()),
        userInfo.getUserName());
  }

  @Override
  public void deleteAll() {
    final String sql = "DELETE FROM " + TABLE_NAME;
    jdbcTemplate.execute(sql);
  }

  private Map<String, Object> toMap(UserInfo userInfo) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("user_name", userInfo.getUserName());
    parameters.put("user_password", userInfo.getUserPassword());
    return parameters;
  }

  private static class UserInfoRowMapper implements RowMapper<UserInfo> {

    @Override
    public UserInfo mapRow(ResultSet resultSet, int i) throws SQLException {
      return new UserInfo(
          resultSet.getString("user_name"),
          resultSet.getString("user_password"));
    }
  }
}
