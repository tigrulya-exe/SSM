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
import org.smartdata.metastore.dao.AbstractDao;
import org.smartdata.metastore.dao.CompressionFileDao;
import org.smartdata.model.CompressionFileState;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CompressionFileDao.
 */
public class DefaultCompressionFileDao extends AbstractDao implements CompressionFileDao {
  private static final String TABLE_NAME = "compression_file";

  public DefaultCompressionFileDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public void insert(CompressionFileState compressionInfo) {
    insert(compressionInfo, this::toMap);
  }

  @Override
  public void insertUpdate(CompressionFileState compressionInfo) {
    Gson gson = new Gson();
    String sql = "REPLACE INTO " + TABLE_NAME
        + "(path, buffer_size, compression_impl, "
        + "original_length, compressed_length, originalPos, compressedPos)"
        + " VALUES(?,?,?,?,?,?,?);";
    jdbcTemplate.update(sql, compressionInfo.getPath(),
        compressionInfo.getBufferSize(),
        compressionInfo.getCompressionImpl(),
        compressionInfo.getOriginalLength(),
        compressionInfo.getCompressedLength(),
        gson.toJson(compressionInfo.getOriginalPos()),
        gson.toJson(compressionInfo.getCompressedPos()));
  }

  @Override
  public void deleteByPath(String filePath) {
    final String sql = "DELETE FROM " + TABLE_NAME + " WHERE path = ?";
    jdbcTemplate.update(sql, filePath);
  }

  @Override
  public void deleteAll() {
    final String sql = "DELETE FROM " + TABLE_NAME;
    jdbcTemplate.execute(sql);
  }

  @Override
  public List<CompressionFileState> getAll() {
    return jdbcTemplate.query("SELECT * FROM " + TABLE_NAME,
        new CompressFileRowMapper());
  }

  @Override
  public CompressionFileState getInfoByPath(String filePath) {
    return jdbcTemplate.queryForObject("SELECT * FROM " + TABLE_NAME + " WHERE path = ?",
        new Object[]{filePath}, new CompressFileRowMapper());
  }

  private Map<String, Object> toMap(CompressionFileState compressionInfo) {
    Gson gson = new Gson();
    Map<String, Object> parameters = new HashMap<>();
    Long[] originalPos = compressionInfo.getOriginalPos();
    Long[] compressedPos = compressionInfo.getCompressedPos();
    String originalPosGson = gson.toJson(originalPos);
    String compressedPosGson = gson.toJson(compressedPos);
    parameters.put("path", compressionInfo.getPath());
    parameters.put("buffer_size", compressionInfo.getBufferSize());
    parameters.put("compression_impl", compressionInfo.getCompressionImpl());
    parameters.put("original_length", compressionInfo.getOriginalLength());
    parameters.put("compressed_length", compressionInfo.getCompressedLength());
    parameters.put("originalPos", originalPosGson);
    parameters.put("compressedPos", compressedPosGson);
    return parameters;
  }

  private static class CompressFileRowMapper implements RowMapper<CompressionFileState> {
    @Override
    public CompressionFileState mapRow(ResultSet resultSet, int i) throws SQLException {
      Gson gson = new Gson();
      String originalPosGson = resultSet.getString("originalPos");
      String compressedPosGson = resultSet.getString("compressedPos");
      Long[] originalPos = gson.fromJson(originalPosGson, new TypeToken<Long[]>() {
      }.getType());
      Long[] compressedPos = gson.fromJson(compressedPosGson, new TypeToken<Long[]>() {
      }.getType());
      CompressionFileState compressionInfo =
          CompressionFileState.newBuilder()
              .setFileName(resultSet.getString("path"))
              .setBufferSize(resultSet.getInt("buffer_size"))
              .setCompressImpl(resultSet.getString("compression_impl"))
              .setOriginalLength(resultSet.getLong("original_length"))
              .setCompressedLength(resultSet.getLong("compressed_length"))
              .setOriginalPos(originalPos)
              .setCompressedPos(compressedPos)
              .build();
      return compressionInfo;
    }
  }
}
