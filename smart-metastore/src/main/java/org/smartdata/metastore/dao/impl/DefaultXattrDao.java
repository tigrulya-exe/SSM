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
import org.smartdata.metastore.dao.XattrDao;
import org.smartdata.model.XAttribute;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultXattrDao extends AbstractDao implements XattrDao {
  private static final String TABLE_NAME = "xattr";

  public DefaultXattrDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public List<XAttribute> getXattrList(Long fid) {
    String sql =
        String.format("SELECT * FROM xattr WHERE fid = %s;", fid);
    return getXattrList(sql);
  }

  private List<XAttribute> getXattrList(String sql) {
    List<XAttribute> list = new LinkedList<>();
    List<Map<String, Object>> maplist = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> map : maplist) {
      list.add(new XAttribute((String) map.get("namespace"),
          (String) map.get("name"), (byte[]) map.get("value")));
    }
    return list;
  }

  @Override
  public synchronized boolean insertXattrList(final Long fid, final List<XAttribute> attributes)
      throws SQLException {
    String sql = "INSERT INTO xattr (fid, namespace, name, value) VALUES (?, ?, ?, ?)";
    int[] i = jdbcTemplate.batchUpdate(sql,
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setLong(1, fid);
            ps.setString(2, attributes.get(i).getNameSpace());
            ps.setString(3, attributes.get(i).getName());
            ps.setBytes(4, attributes.get(i).getValue());
          }

          public int getBatchSize() {
            return attributes.size();
          }
        });
    return i.length == attributes.size();
  }
}
