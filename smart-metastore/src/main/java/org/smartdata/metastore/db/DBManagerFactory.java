package org.smartdata.metastore.db;

import org.apache.hadoop.conf.Configuration;
import org.smartdata.metastore.DBPool;
import org.smartdata.metastore.MetaStoreException;

import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_DB_URL_KEY;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_CHANGELOG_PATH_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_CHANGELOG_PATH_KEY;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_LABELS_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_LABELS_KEY;
import static org.smartdata.metastore.utils.MetaStoreUtils.isOldMySql;

public class DBManagerFactory {
    public static final String OLD_MYSQL_LABEL = "old_mysql";

    public DBManager createDbManager(DBPool dbPool, Configuration conf) throws MetaStoreException {
        String changelogPath = conf.get(
                SMART_METASTORE_MIGRATION_CHANGELOG_PATH_KEY,
                SMART_METASTORE_MIGRATION_CHANGELOG_PATH_DEFAULT);

        String labels = conf.get(
                SMART_METASTORE_MIGRATION_LABELS_KEY,
                SMART_METASTORE_MIGRATION_LABELS_DEFAULT);

        String dbUrl = conf.get(SMART_METASTORE_DB_URL_KEY);
        if (isOldMySql(dbPool, dbUrl)) {
            labels = appendToLabels(labels, OLD_MYSQL_LABEL);
        }

        return new LiquibaseDBManager(dbPool, changelogPath, labels);
    }

    private String appendToLabels(String currentLabels, String label) {
        if (currentLabels.isEmpty()) {
            return label;
        }

        return String.format("(%s) OR %s", currentLabels, label);
    }

}
