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
import org.smartdata.metastore.dao.SmallFileDao;
import org.smartdata.model.CompactFileState;
import org.smartdata.model.FileContainerInfo;
import org.smartdata.model.FileState;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DefaultSmallFileDao extends AbstractDao implements SmallFileDao {
  private static final String TABLE_NAME = "small_file";

  public DefaultSmallFileDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public void insertUpdate(CompactFileState compactFileState) {
    String sql = "REPLACE INTO small_file (path, container_file_path, offset, length)"
        + " VALUES (?,?,?,?)";
    jdbcTemplate.update(sql, compactFileState.getPath(),
        compactFileState.getFileContainerInfo().getContainerFilePath(),
        compactFileState.getFileContainerInfo().getOffset(),
        compactFileState.getFileContainerInfo().getLength());
  }

  @Override
  public int[] batchInsertUpdate(final CompactFileState[] fileStates) {
    String sql = "REPLACE INTO small_file (path, container_file_path, offset, length)"
        + " VALUES (?,?,?,?)";
    return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps,
                            int i) throws SQLException {
        ps.setString(1, fileStates[i].getPath());
        ps.setString(2, fileStates[i].getFileContainerInfo().getContainerFilePath());
        ps.setLong(3, fileStates[i].getFileContainerInfo().getOffset());
        ps.setLong(4, fileStates[i].getFileContainerInfo().getLength());
      }

      @Override
      public int getBatchSize() {
        return fileStates.length;
      }
    });
  }

  @Override
  public void deleteByPath(String path, boolean recursive) {
    String sql = "DELETE FROM small_file WHERE path = ?";
    jdbcTemplate.update(sql, path);
    if (recursive) {
      sql = "DELETE FROM small_file WHERE path LIKE ?";
      jdbcTemplate.update(sql, path + "/%");
    }
  }

  @Override
  public int[] batchDelete(final List<String> paths) {
    final String sql = "DELETE FROM small_file WHERE path = ?";
    return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setString(1, paths.get(i));
      }

      @Override
      public int getBatchSize() {
        return paths.size();
      }
    });
  }

  @Override
  public FileState getFileStateByPath(String path) {
    return jdbcTemplate.queryForObject("SELECT * FROM small_file WHERE path = ?",
        new Object[]{path}, new FileStateRowMapper());
  }

  @Override
  public List<String> getSmallFilesByContainerFile(String containerFilePath) {
    String sql = "SELECT path FROM small_file where container_file_path = ?";
    return jdbcTemplate.queryForList(sql, String.class, containerFilePath);
  }

  @Override
  public List<String> getAllContainerFiles() {
    String sql = "SELECT DISTINCT container_file_path FROM small_file";
    return jdbcTemplate.queryForList(sql, String.class);
  }

  private static class FileStateRowMapper implements RowMapper<FileState> {
    @Override
    public FileState mapRow(ResultSet resultSet, int i)
        throws SQLException {
      return new CompactFileState(resultSet.getString("path"),
          new FileContainerInfo(
              resultSet.getString("container_file_path"),
              resultSet.getLong("offset"),
              resultSet.getLong("length"))
      );
    }
  }
}
