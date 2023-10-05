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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringEscapeUtils;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.dao.AbstractDao;
import org.smartdata.metastore.dao.ActionDao;
import org.smartdata.metastore.utils.MetaStoreUtils;
import org.smartdata.model.ActionInfo;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultActionDao extends AbstractDao implements ActionDao {

  private static final String TABLE_NAME = "action";
  private static final String RUNNING_TIME = "running_time";
  private final List<String> tableColumns;

  public DefaultActionDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
    try (Connection conn = dataSource.getConnection()) {
      tableColumns = MetaStoreUtils.getTableColumns(conn, "action");
    } catch (SQLException | MetaStoreException e) {
      throw new RuntimeException(e);
    }
    tableColumns.add(RUNNING_TIME);
  }

  @Override
  public List<ActionInfo> getAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return jdbcTemplate.query("SELECT * FROM " + TABLE_NAME,
        new ActionRowMapper());
  }

  @Override
  public Long getCountOfAction() {
    String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
    return jdbcTemplate.queryForObject(sql, Long.class);
  }

  @Override
  public ActionInfo getById(long aid) {
    return jdbcTemplate.queryForObject(
        "SELECT * FROM " + TABLE_NAME + " WHERE aid = ?",
        new Object[]{aid},
        new ActionRowMapper());
  }

  @Override
  public List<ActionInfo> getByIds(List<Long> aids) {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate =
        new NamedParameterJdbcTemplate(dataSource);
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue("aids", aids);
    return namedParameterJdbcTemplate.query(
        "SELECT * FROM " + TABLE_NAME + " WHERE aid IN (:aids)",
        parameterSource,
        new ActionRowMapper());
  }

  @Override
  public List<ActionInfo> getByCid(long cid) {
    return jdbcTemplate.query(
        "SELECT * FROM " + TABLE_NAME + " WHERE cid = ?",
        new Object[]{cid},
        new ActionRowMapper());
  }

  @Override
  public List<ActionInfo> getByCondition(String aidCondition,
                                         String cidCondition) {
    String sqlPrefix = "SELECT * FROM " + TABLE_NAME + " WHERE ";
    String sqlAid = (aidCondition == null) ? "" : "AND aid " + aidCondition;
    String sqlCid = (cidCondition == null) ? "" : "AND cid " + cidCondition;
    String sqlFinal = "";
    if (aidCondition != null || cidCondition != null) {
      sqlFinal = sqlPrefix + sqlAid + sqlCid;
      sqlFinal = sqlFinal.replaceFirst("AND ", "");
    } else {
      sqlFinal = sqlPrefix.replaceFirst("WHERE ", "");
    }
    return jdbcTemplate.query(sqlFinal, new ActionRowMapper());
  }

  @Override
  public List<ActionInfo> getLatestActions(int size) {
    if (size != 0) {
      jdbcTemplate.setMaxRows(size);
    }
    String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY aid DESC";
    return jdbcTemplate.query(sql, new ActionRowMapper());
  }

  @Override
  public List<ActionInfo> getLatestActions(String actionName, int size) {
    if (size != 0) {
      jdbcTemplate.setMaxRows(size);
    }
    String sql = "SELECT * FROM " + TABLE_NAME + " WHERE action_name = ? ORDER BY aid DESC";
    return jdbcTemplate.query(sql, new ActionRowMapper(), actionName);
  }

  @Override
  public List<ActionInfo> getLatestActions(String actionName, int size,
                                           boolean successful, boolean finished) {
    if (size != 0) {
      jdbcTemplate.setMaxRows(size);
    }
    String sql =
        "SELECT * FROM "
            + TABLE_NAME
            +
            " WHERE action_name = ? AND successful = ? AND finished = ? ORDER BY aid DESC";
    return jdbcTemplate.query(sql, new ActionRowMapper(), actionName, successful, finished);
  }

  @Override
  public List<ActionInfo> getLatestActions(String actionName, boolean successful,
                                           int size) {
    if (size != 0) {
      jdbcTemplate.setMaxRows(size);
    }
    String sql =
        "SELECT * FROM "
            + TABLE_NAME
            + " WHERE action_name = ? AND successful = ? ORDER BY aid DESC";
    return jdbcTemplate.query(sql, new ActionRowMapper(), actionName, successful);
  }

  @Override
  public List<ActionInfo> getAPageOfAction(long start, long offset, List<String> orderBy,
                                           List<Boolean> isDesc) {
    boolean ifHasAid = false;
    String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY ";

    for (int i = 0; i < orderBy.size(); i++) {
      String ob = orderBy.get(i);
      if (!tableColumns.contains(ob)) {
        continue;
      }

      if (ob.equals("aid")) {
        ifHasAid = true;
      }

      if (ob.equals(RUNNING_TIME)) {
        sql = sql + "(finish_time - create_time)";
      } else {
        sql = sql + ob;
      }
      if (isDesc.size() > i) {
        if (isDesc.get(i)) {
          sql = sql + " desc ";
        }
        sql = sql + ",";
      }
    }

    if (!ifHasAid) {
      sql = sql + "aid,";
    }

    //delete the last char
    sql = sql.substring(0, sql.length() - 1);
    //add limit
    sql = sql + " LIMIT " + start + "," + offset + ";";
    return jdbcTemplate.query(sql, new ActionRowPartMapper());
  }

  @Override
  public List<ActionInfo> getAPageOfAction(long start, long offset) {
    String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT " + start + "," + offset + ";";
    return jdbcTemplate.query(sql, new ActionRowPartMapper());
  }

  @Override
  public List<ActionInfo> searchAction(String path, long start, long offset, List<String> orderBy,
                                       List<Boolean> isDesc, long[] retTotalNumActions) {
    List<ActionInfo> ret;
    boolean ifHasAid = false;
    String sqlFilter = TABLE_NAME + " WHERE ("
        + "aid LIKE '%" + path + "%' ESCAPE '/' "
        + "OR cid LIKE '%" + path + "%' ESCAPE '/' "
        + "OR args LIKE '%" + path + "%' ESCAPE '/' "
        + "OR result LIKE '%" + path + "%' ESCAPE '/' "
        + "OR exec_host LIKE '%" + path + "%' ESCAPE '/' "
        + "OR progress LIKE '%" + path + "%' ESCAPE '/' "
        + "OR log LIKE '%" + path + "%' ESCAPE '/' "
        + "OR action_name LIKE '%" + path + "%' ESCAPE '/')";
    String sql = "SELECT * FROM " + sqlFilter;
    String sqlCount = "SELECT count(*) FROM " + sqlFilter + ";";
    if (orderBy.size() == 0) {
      sql += " LIMIT " + start + "," + offset + ";";
      ret = jdbcTemplate.query(sql, new ActionRowMapper());
    } else {
      sql += " ORDER BY ";

      for (int i = 0; i < orderBy.size(); i++) {
        String ob = orderBy.get(i);
        if (!tableColumns.contains(ob)) {
          continue;
        }

        if (ob.equals("aid")) {
          ifHasAid = true;
        }

        if (ob.equals(RUNNING_TIME)) {
          sql = sql + "(finish_time - create_time)";
        } else {
          sql = sql + ob;
        }

        if (isDesc.size() > i) {
          if (isDesc.get(i)) {
            sql = sql + " desc ";
          }
          sql = sql + ",";
        }
      }

      if (!ifHasAid) {
        sql = sql + "aid,";
      }

      //delete the last char
      sql = sql.substring(0, sql.length() - 1);
      //add limit
      sql = sql + " LIMIT " + start + "," + offset + ";";
      ret = jdbcTemplate.query(sql, new ActionRowMapper());
    }
    if (retTotalNumActions != null) {
      retTotalNumActions[0] = jdbcTemplate.queryForObject(sqlCount, Long.class);
    }
    return ret;
  }

  @Override
  public List<ActionInfo> getLatestActions(String actionType, int size,
                                           boolean finished) {
    if (size != 0) {
      jdbcTemplate.setMaxRows(size);
    }
    String sql =
        "SELECT * FROM " + TABLE_NAME
            + " WHERE action_name = ? AND finished = ? ORDER BY aid DESC";
    return jdbcTemplate.query(sql, new ActionRowMapper(), actionType, finished);
  }

  @Override
  public void delete(long aid) {
    final String sql = "DELETE FROM " + TABLE_NAME + " WHERE aid = ?";
    jdbcTemplate.update(sql, aid);
  }

  @Override
  public void deleteCmdletActions(long cid) {
    final String sql = "DELETE FROM " + TABLE_NAME + " WHERE cid = ?";
    jdbcTemplate.update(sql, cid);
  }

  @Override
  public int[] batchDeleteCmdletActions(final List<Long> cids) {
    final String sql = "DELETE FROM " + TABLE_NAME + " WHERE cid = ?";
    return jdbcTemplate.batchUpdate(
        sql,
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setLong(1, cids.get(i));
          }

          public int getBatchSize() {
            return cids.size();
          }
        });
  }

  @Override
  public void deleteAll() {
    final String sql = "DELETE FROM " + TABLE_NAME;
    jdbcTemplate.execute(sql);
  }

  @Override
  public void insert(ActionInfo actionInfo) {
    insert(actionInfo, this::toMap);
  }

  @Override
  public void insert(ActionInfo[] actionInfos) {
    insert(actionInfos, this::toMap);
  }

  @Override
  public int[] replace(final ActionInfo[] actionInfos) {
    String sql =
        "REPLACE INTO "
            + TABLE_NAME
            + "(aid, "
            + "cid, "
            + "action_name, "
            + "args, "
            + "result, "
            + "log, "
            + "successful, "
            + "create_time, "
            + "finished, "
            + "finish_time, "
            + "exec_host, "
            + "progress)"
            + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return jdbcTemplate.batchUpdate(sql,
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps,
                                int i) throws SQLException {
            ps.setLong(1, actionInfos[i].getActionId());
            ps.setLong(2, actionInfos[i].getCmdletId());
            ps.setString(3, actionInfos[i].getActionName());
            ps.setString(4, actionInfos[i].getArgsJsonString());
            ps.setString(5, actionInfos[i].getResult());
            ps.setString(6, actionInfos[i].getLog());
            ps.setBoolean(7, actionInfos[i].isSuccessful());
            ps.setLong(8, actionInfos[i].getCreateTime());
            ps.setBoolean(9, actionInfos[i].isFinished());
            ps.setLong(10, actionInfos[i].getFinishTime());
            ps.setString(11, actionInfos[i].getExecHost());
            ps.setFloat(12, actionInfos[i].getProgress());
          }

          public int getBatchSize() {
            return actionInfos.length;
          }
        });
  }

  @Override
  public int update(final ActionInfo actionInfo) {
    return update(new ActionInfo[]{actionInfo})[0];
  }

  @Override
  public int[] update(final ActionInfo[] actionInfos) {
    String sql =
        "UPDATE "
            + TABLE_NAME
            + " SET "
            + "result = ?, "
            + "log = ?, "
            + "successful = ?, "
            + "create_time = ?, "
            + "finished = ?, "
            + "finish_time = ?, "
            + "exec_host = ?, "
            + "progress = ? "
            + "WHERE aid = ?";
    return jdbcTemplate.batchUpdate(sql,
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps,
                                int i) throws SQLException {
            ps.setString(1, actionInfos[i].getResult());
            ps.setString(2, actionInfos[i].getLog());
            ps.setBoolean(3, actionInfos[i].isSuccessful());
            ps.setLong(4, actionInfos[i].getCreateTime());
            ps.setBoolean(5, actionInfos[i].isFinished());
            ps.setLong(6, actionInfos[i].getFinishTime());
            ps.setString(7, actionInfos[i].getExecHost());
            ps.setFloat(8, actionInfos[i].getProgress());
            ps.setLong(9, actionInfos[i].getActionId());
          }

          public int getBatchSize() {
            return actionInfos.length;
          }
        });
  }

  @Override
  public long getMaxId() {
    Long ret = jdbcTemplate
        .queryForObject("SELECT MAX(aid) FROM " + TABLE_NAME, Long.class);
    if (ret == null) {
      return 0;
    } else {
      return ret + 1;
    }
  }

  private Map<String, Object> toMap(ActionInfo actionInfo) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("aid", actionInfo.getActionId());
    parameters.put("cid", actionInfo.getCmdletId());
    parameters.put("action_name", actionInfo.getActionName());
    parameters.put("args", actionInfo.getArgsJsonString());
    parameters
        .put("result", StringEscapeUtils.escapeJava(actionInfo.getResult()));
    parameters.put("log", StringEscapeUtils.escapeJava(actionInfo.getLog()));
    parameters.put("successful", actionInfo.isSuccessful());
    parameters.put("create_time", actionInfo.getCreateTime());
    parameters.put("finished", actionInfo.isFinished());
    parameters.put("finish_time", actionInfo.getFinishTime());
    parameters.put("exec_host", actionInfo.getExecHost());
    parameters.put("progress", actionInfo.getProgress());
    return parameters;
  }

  private static class ActionRowMapper implements RowMapper<ActionInfo> {

    @Override
    public ActionInfo mapRow(ResultSet resultSet, int i) throws SQLException {
      ActionInfo actionInfo = new ActionInfo();
      actionInfo.setActionId(resultSet.getLong("aid"));
      actionInfo.setCmdletId(resultSet.getLong("cid"));
      actionInfo.setActionName(resultSet.getString("action_name"));
      actionInfo.setArgsFromJsonString(resultSet.getString("args"));
      actionInfo.setResult(
          StringEscapeUtils.unescapeJava(resultSet.getString("result")));
      actionInfo
          .setLog(StringEscapeUtils.unescapeJava(resultSet.getString("log")));
      actionInfo.setSuccessful(resultSet.getBoolean("successful"));
      actionInfo.setCreateTime(resultSet.getLong("create_time"));
      actionInfo.setFinished(resultSet.getBoolean("finished"));
      actionInfo.setFinishTime(resultSet.getLong("finish_time"));
      actionInfo.setExecHost(resultSet.getString("exec_host"));
      actionInfo.setProgress(resultSet.getFloat("progress"));
      return actionInfo;
    }
  }

  /**
   * No need to set result & log. If arg value is too long, it will be
   * truncated.
   */
  private static class ActionRowPartMapper implements RowMapper<ActionInfo> {
    @Override
    public ActionInfo mapRow(ResultSet resultSet, int i) throws SQLException {
      ActionInfo actionInfo = new ActionInfo();
      actionInfo.setActionId(resultSet.getLong("aid"));
      actionInfo.setCmdletId(resultSet.getLong("cid"));
      actionInfo.setActionName(resultSet.getString("action_name"));
      actionInfo.setArgsFromJsonString(resultSet.getString("args"));
      actionInfo.setArgs(
          getTruncatedArgs(resultSet.getString("args")));
      actionInfo.setSuccessful(resultSet.getBoolean("successful"));
      actionInfo.setCreateTime(resultSet.getLong("create_time"));
      actionInfo.setFinished(resultSet.getBoolean("finished"));
      actionInfo.setFinishTime(resultSet.getLong("finish_time"));
      actionInfo.setExecHost(resultSet.getString("exec_host"));
      actionInfo.setProgress(resultSet.getFloat("progress"));
      return actionInfo;
    }

    public Map<String, String> getTruncatedArgs(String jsonArgs) {
      Gson gson = new Gson();
      Map<String, String> args = gson.fromJson(jsonArgs,
          new TypeToken<Map<String, String>>() {
          }.getType());
      for (Map.Entry<String, String> entry : args.entrySet()) {
        if (entry.getValue().length() > 50) {
          entry.setValue(entry.getValue().substring(0, 50) + "...");
        }
      }
      return args;
    }
  }
}
