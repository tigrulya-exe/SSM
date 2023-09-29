/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.metastore.utils;

import com.mysql.jdbc.NonRegisteringDriver;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.conf.SmartConf;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.metastore.DruidPool;
import org.smartdata.metastore.MetaStore;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.dao.DaoProvider;
import org.smartdata.metastore.dao.impl.DefaultDaoProvider;
import org.smartdata.metastore.db.DBManager;
import org.smartdata.metastore.db.DBManagerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.springframework.jdbc.support.JdbcUtils.closeConnection;

/**
 * Utilities for table operations.
 */
public class MetaStoreUtils {
  public static final String SQLITE_URL_PREFIX = "jdbc:sqlite:";
  public static final String MYSQL_URL_PREFIX = "jdbc:mysql:";
  public static final String[] DB_NAME_NOT_ALLOWED =
      new String[]{
          "mysql",
          "sys",
          "information_schema",
          "INFORMATION_SCHEMA",
          "performance_schema",
          "PERFORMANCE_SCHEMA"
      };
  static final Logger LOG = LoggerFactory.getLogger(MetaStoreUtils.class);

  public static final String[] TABLESET = new String[]{
      "access_count_table",
      "blank_access_count_info",
      "cached_file",
      "ec_policy",
      "file",
      "storage",
      "storage_hist",
      "storage_policy",
      "xattr",
      "datanode_info",
      "datanode_storage_info",
      "rule",
      "cmdlet",
      "action",
      "file_diff",
      "global_config",
      "cluster_config",
      "sys_info",
      "cluster_info",
      "backup_file",
      "file_state",
      "compression_file",
      "small_file",
      "user_info",
      "whitelist"
  };

  public static Connection createConnection(String driver, String url,
                                            String userName,
                                            String password)
      throws ClassNotFoundException, SQLException {
    Class.forName(driver);
    return DriverManager.getConnection(url, userName, password);
  }

  public static Connection createSqliteConnection(String dbFilePath)
      throws MetaStoreException {
    try {
      return createConnection("org.sqlite.JDBC", SQLITE_URL_PREFIX + dbFilePath,
          null, null);
    } catch (Exception e) {
      throw new MetaStoreException(e);
    }
  }

  public static int getTableSetNum(Connection conn, String[] tableSet) throws MetaStoreException {
    String tables = "('" + StringUtils.join(tableSet, "','") + "')";
    try {
      String url = conn.getMetaData().getURL();
      String query;
      if (url.startsWith(MetaStoreUtils.MYSQL_URL_PREFIX)) {
        String dbName = getMysqlDBName(url);
        query = String.format("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
            + "WHERE TABLE_SCHEMA='%s' AND TABLE_NAME IN %s", dbName, tables);
      } else if (url.startsWith(MetaStoreUtils.SQLITE_URL_PREFIX)) {
        query = String.format("SELECT COUNT(*) FROM sqlite_master "
            + "WHERE TYPE='table' AND NAME IN %s", tables);
      } else {
        throw new MetaStoreException("The jdbc url is not valid for SSM use.");
      }

      int num = 0;
      Statement s = conn.createStatement();
      ResultSet rs = s.executeQuery(query);
      if (rs.next()) {
        num = rs.getInt(1);
      }
      return num;
    } catch (Exception e) {
      throw new MetaStoreException(e);
    } finally {
      closeConnection(conn);
    }
  }

  public static void formatDatabase(SmartConf conf) throws MetaStoreException {
    getDBAdapter(conf).formatDataBase();
  }

  public static void checkTables(SmartConf conf) throws MetaStoreException {
    getDBAdapter(conf).checkTables();
  }

  public static String getMysqlDBName(String url) throws SQLException {
    NonRegisteringDriver nonRegisteringDriver = new NonRegisteringDriver();
    Properties properties = nonRegisteringDriver.parseURL(url, null);
    return properties.getProperty(NonRegisteringDriver.DBNAME_PROPERTY_KEY);
  }

  public static MetaStore getDBAdapter(
      SmartConf conf) throws MetaStoreException {
    URL pathUrl = ClassLoader.getSystemResource("");
    String path = pathUrl.getPath();

    String fileName = "druid.xml";
    String expectedCpPath = path + fileName;
    LOG.info("Expected DB connection pool configuration path = "
        + expectedCpPath);
    File cpConfigFile = new File(expectedCpPath);
    if (cpConfigFile.exists()) {
      LOG.info("Using pool configure file: " + expectedCpPath);
      Properties p = new Properties();
      try {
        p.loadFromXML(new FileInputStream(cpConfigFile));

        String url = conf.get(SmartConfKeys.SMART_METASTORE_DB_URL_KEY);
        if (url != null) {
          p.setProperty("url", url);
        }

        String purl = p.getProperty("url");
        if (purl == null || purl.isEmpty()) {
          purl = getDefaultSqliteDB(); // For testing
          p.setProperty("url", purl);
          LOG.warn("Database URL not specified, using " + purl);
        }

        if (purl.startsWith(MetaStoreUtils.MYSQL_URL_PREFIX)) {
          String dbName = getMysqlDBName(purl);
          for (String name : DB_NAME_NOT_ALLOWED) {
            if (dbName.equals(name)) {
              throw new MetaStoreException(
                  String.format(
                      "The database %s in mysql is for DB system use, "
                          + "please appoint other database in druid.xml.",
                      name));
            }
          }
        }

        try {
          String pw = conf
              .getPasswordFromHadoop(SmartConfKeys.SMART_METASTORE_PASSWORD);
          if (pw != null && pw != "") {
            p.setProperty("password", pw);
          }
        } catch (IOException e) {
          LOG.info("Can not get metastore password from hadoop provision credentials,"
              + " use the one configured in druid.xml .");
        }

        for (String key : p.stringPropertyNames()) {
          if (key.equals("password")) {
            LOG.info("\t" + key + " = **********");
          } else {
            LOG.info("\t" + key + " = " + p.getProperty(key));
          }
        }
        DruidPool druidPool = new DruidPool(p);
        DBManager dbManager = new DBManagerFactory().createDbManager(druidPool, conf);
        DaoProvider daoProvider = new DefaultDaoProvider(druidPool);
        return new MetaStore(druidPool, dbManager, daoProvider);
      } catch (Exception e) {
        if (e instanceof InvalidPropertiesFormatException) {
          throw new MetaStoreException(
              "Malformat druid.xml, please check the file.", e);
        } else {
          throw new MetaStoreException(e);
        }
      }
    } else {
      LOG.info("DB connection pool config file " + expectedCpPath
          + " NOT found.");
    }
    // Get Default configure from druid-template.xml
    fileName = "druid-template.xml";
    expectedCpPath = path + fileName;
    LOG.info("Expected DB connection pool configuration path = "
        + expectedCpPath);
    cpConfigFile = new File(expectedCpPath);
    LOG.info("Using pool configure file: " + expectedCpPath);
    Properties p = new Properties();
    try {
      p.loadFromXML(new FileInputStream(cpConfigFile));
    } catch (Exception e) {
      throw new MetaStoreException(e);
    }
    String url = conf.get(SmartConfKeys.SMART_METASTORE_DB_URL_KEY);
    if (url != null) {
      p.setProperty("url", url);
    }
    for (String key : p.stringPropertyNames()) {
      LOG.info("\t" + key + " = " + p.getProperty(key));
    }
    DruidPool druidPool = new DruidPool(p);
    DBManager dbManager = new DBManagerFactory().createDbManager(druidPool, conf);
    DaoProvider daoProvider = new DefaultDaoProvider(druidPool);
    return new MetaStore(druidPool, dbManager, daoProvider);
  }

  public static Integer getKey(Map<Integer, String> map, String value) {
    for (Integer key : map.keySet()) {
      if (map.get(key).equals(value)) {
        return key;
      }
    }
    return null;
  }

  /**
   * Retrieve table column names.
   *
   * @param conn
   * @param tableName
   * @return
   * @throws MetaStoreException
   */
  public static List<String> getTableColumns(Connection conn, String tableName)
      throws MetaStoreException {
    List<String> ret = new ArrayList<>();
    try {
      ResultSet res = conn.getMetaData().getColumns(null, null, tableName, null);
      while (res.next()) {
        ret.add(res.getString("COLUMN_NAME"));
      }
      return ret;
    } catch (Exception e) {
      throw new MetaStoreException(e);
    }
  }

  /**
   * This default behavior provided here is mainly for convenience.
   */
  private static String getDefaultSqliteDB() {
    String absFilePath = System.getProperty("user.home")
        + "/smart-test-default.db";
    return MetaStoreUtils.SQLITE_URL_PREFIX + absFilePath;
  }
}

