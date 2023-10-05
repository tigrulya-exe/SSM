package org.smartdata.metastore.dao.impl;

import org.smartdata.metastore.dao.AbstractDao;
import org.smartdata.metastore.dao.WhitelistDao;

import javax.sql.DataSource;

public class DefaultWhitelistDao extends AbstractDao implements WhitelistDao {
  private static final String TABLE_NAME = "whitelist";

  public DefaultWhitelistDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public String getLastFetchedDirs() {
    String sql = "SELECT * FROM " + TABLE_NAME;
    return jdbcTemplate.queryForObject(sql, String.class);
  }

  @Override
  public void updateTable(String newWhitelist) {
    final String sql = "UPDATE whitelist SET last_fetched_dirs =?";
    jdbcTemplate.update(sql, newWhitelist);
  }
}
