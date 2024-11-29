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
package org.smartdata.conf;

import org.smartdata.metrics.impl.SmartServerAccessEventSource;

/**
 * This class contains the configure keys needed by SSM.
 */
public class SmartConfKeys {
    public static final String SMART_CONF_KEYS_PREFIX = "smart.";
    public static final String SMART_DFS_ENABLED = "smart.dfs.enabled";
    public static final boolean SMART_DFS_ENABLED_DEFAULT = true;

    public static final String SMART_CONF_DIR_KEY = "smart.conf.dir";
    public static final String SMART_HADOOP_CONF_DIR_KEY = "smart.hadoop.conf.path";
    public static final String SMART_CONF_DIR_DEFAULT = "conf";

    public static final String SMART_NAMESPACE_FETCHER_BATCH_KEY = "smart.namespace.fetcher.batch";
    public static final int SMART_NAMESPACE_FETCHER_BATCH_DEFAULT = 500;

    public static final String SMART_DFS_NAMENODE_RPCSERVER_KEY = "smart.dfs.namenode.rpcserver";

    // Configure keys for HDFS
    public static final String SMART_NAMESPACE_FETCHER_IGNORE_UNSUCCESSIVE_INOTIFY_EVENT_KEY =
            "smart.namespace.fetcher.ignore.unsuccessive.inotify.event";
    public static final boolean SMART_NAMESPACE_FETCHER_IGNORE_UNSUCCESSIVE_INOTIFY_EVENT_DEFAULT =
            false;
    public static final String SMART_NAMESPACE_FETCHER_PRODUCERS_NUM_KEY =
            "smart.namespace.fetcher.producers.num";
    public static final int SMART_NAMESPACE_FETCHER_PRODUCERS_NUM_DEFAULT = 3;
    public static final String SMART_NAMESPACE_FETCHER_CONSUMERS_NUM_KEY =
            "smart.namespace.fetcher.consumers.num";
    public static final int SMART_NAMESPACE_FETCHER_CONSUMERS_NUM_DEFAULT = 3;

    // SSM
    public static final String SMART_SERVER_RPC_ADDRESS_KEY = "smart.server.rpc.address";
    public static final String SMART_SERVER_RPC_ADDRESS_DEFAULT = "0.0.0.0:7042";
    public static final String SMART_SERVER_RPC_HANDLER_COUNT_KEY = "smart.server.rpc.handler.count";
    public static final int SMART_SERVER_RPC_HANDLER_COUNT_DEFAULT = 80;

    public static final String SMART_REST_SERVER_PORT_KEY = "smart.rest.server.port";
    public static final int SMART_REST_SERVER_PORT_KEY_DEFAULT = 8081;
    public static final String SMART_AGENT_HTTP_SERVER_PORT_KEY = "smart.agent.http.server.port";
    public static final int SMART_AGENT_HTTP_SERVER_PORT_DEFAULT = 8081;
    public static final String SMART_SECURITY_ENABLE = "smart.security.enable";
    public static final String SMART_SERVER_KEYTAB_FILE_KEY = "smart.server.keytab.file";
    public static final String SMART_SERVER_KERBEROS_PRINCIPAL_KEY =
            "smart.server.kerberos.principal";
    public static final String SMART_AGENT_KEYTAB_FILE_KEY = "smart.agent.keytab.file";
    public static final String SMART_AGENT_KERBEROS_PRINCIPAL_KEY =
            "smart.agent.kerberos.principal";
    public static final String SMART_SECURITY_CLIENT_PROTOCOL_ACL =
            "smart.security.client.protocol.acl";
    public static final String SMART_METASTORE_DB_URL_KEY = "smart.metastore.db.url";
    // Password which get from hadoop credentialProvider used for metastore connect
    public static final String SMART_METASTORE_PASSWORD = "smart.metastore.password";

  public static final String SMART_METASTORE_MIGRATION_CHANGELOG_PATH_KEY =
      "smart.metastore.migration.liquibase.changelog.path";
  public static final String SMART_METASTORE_MIGRATION_CHANGELOG_PATH_DEFAULT =
      "db/changelog/changelog-root.xml";

    public static final String SMART_ACCESS_COUNT_AGGREGATOR_FAILOVER_KEY =
            "smart.file.access.count.aggregator.failover";
    public static final String SMART_ACCESS_COUNT_AGGREGATOR_FAILOVER_MAX_RETRIES_KEY =
            "smart.file.access.count.aggregator.failover.retry.count";
    public static final int SMART_ACCESS_COUNT_AGGREGATOR_FAILOVER_MAX_RETRIES_DEFAULT = 60;

    public static final String SMART_FILE_ACCESS_PARTITIONS_RETENTION_POLICY_KEY =
            "smart.file.access.partition.retention.policy";

    public static final String SMART_ACCESS_EVENT_FETCH_INTERVAL_MS_KEY =
            "smart.file.access.event.fetch.interval.ms";
    public static final long SMART_ACCESS_EVENT_FETCH_INTERVAL_MS_DEFAULT = 1000L;

    public static final String SMART_CACHED_FILE_FETCH_INTERVAL_MS_KEY =
            "smart.cached.file.fetch.interval.ms";
    public static final long SMART_CACHED_FILE_FETCH_INTERVAL_MS_DEFAULT = 5 * 1000L;

    public static final String SMART_NAMESPACE_FETCH_INTERVAL_MS_KEY =
            "smart.namespace.fetch.interval.ms";
    public static final long SMART_NAMESPACE_FETCH_INTERVAL_MS_DEFAULT = 1L;

    // File access partitions
  public static final String SMART_FILE_ACCESS_PARTITIONS_RETENTION_COUNT_KEY =
      "smart.file.access.partition.retention.count";
  public static final int SMART_FILE_ACCESS_PARTITIONS_RETENTION_COUNT_DEFAULT = 24;

  // StatesManager

    // RuleManager
    public static final String SMART_RULE_EXECUTORS_KEY = "smart.rule.executors";
    public static final int SMART_RULE_EXECUTORS_DEFAULT = 5;

    public static final String SMART_CMDLET_EXECUTORS_KEY = "smart.cmdlet.executors";
    public static final int SMART_CMDLET_EXECUTORS_DEFAULT = 10;
    public static final String SMART_DISPATCH_CMDLETS_EXTRA_NUM_KEY =
            "smart.dispatch.cmdlets.extra.num";
    public static final int SMART_DISPATCH_CMDLETS_EXTRA_NUM_DEFAULT = 10;

    public static final String SMART_SYNC_FILE_EQUALITY_STRATEGY =
            "smart.sync.file.equality.strategy";
    public static final String SMART_SYNC_FILE_EQUALITY_STRATEGY_DEFAULT = "CHECKSUM";

    // Cmdlets
    public static final String SMART_CMDLET_MAX_NUM_PENDING_KEY =
            "smart.cmdlet.max.num.pending";
    public static final int SMART_CMDLET_MAX_NUM_PENDING_DEFAULT = 20000;
    public static final String SMART_CMDLET_HIST_MAX_NUM_RECORDS_KEY =
            "smart.cmdlet.hist.max.num.records";
    public static final int SMART_CMDLET_HIST_MAX_NUM_RECORDS_DEFAULT =
            100000;
    public static final String SMART_CMDLET_HIST_MAX_RECORD_LIFETIME_KEY =
            "smart.cmdlet.hist.max.record.lifetime";
    public static final String SMART_CMDLET_HIST_MAX_RECORD_LIFETIME_DEFAULT =
            "30day";
    public static final String SMART_CMDLET_CACHE_BATCH =
            "smart.cmdlet.cache.batch";
    public static final int SMART_CMDLET_CACHE_BATCH_DEFAULT =
            600;
    public static final String SMART_CMDLET_MOVER_MAX_CONCURRENT_BLOCKS_PER_SRV_INST_KEY =
            "smart.cmdlet.mover.max.concurrent.blocks.per.srv.inst";
    public static final int SMART_CMDLET_MOVER_MAX_CONCURRENT_BLOCKS_PER_SRV_INST_DEFAULT = 0;

    // Schedulers
    public static final String SMART_COPY_SCHEDULER_BASE_SYNC_BATCH_KEY =
            "smart.copy.scheduler.base.sync.batch";
    public static final int SMART_COPY_SCHEDULER_BASE_SYNC_BATCH_DEFAULT =
            500;
    public static final String SMART_COPY_SCHEDULER_CHECK_INTERVAL_KEY =
            "smart.copy.scheduler.check.interval.ms";
    public static final int SMART_COPY_SCHEDULER_CHECK_INTERVAL_DEFAULT =
            500;
    public static final String SMART_COPY_SCHEDULER_FILE_DIFF_ARCHIVE_SIZE_KEY =
        "smart.copy.scheduler.diff.archive.size";
    public static final int SMART_COPY_SCHEDULER_FILE_DIFF_ARCHIVE_SIZE_DEFAULT =
        1000;
    public static final String SMART_COPY_SCHEDULER_APPEND_CHAIN_MERGE_SIZE_KEY =
        "smart.copy.scheduler.diff.chain.append.merge.threshold.size";
    public static final long SMART_COPY_SCHEDULER_APPEND_CHAIN_MERGE_SIZE_DEFAULT =
        // default block size in bytes x 3
        134217728L * 3;
    public static final String SMART_COPY_SCHEDULER_APPEND_CHAIN_MERGE_COUNT_KEY =
        "smart.copy.scheduler.diff.chain.append.merge.threshold.count";
    public static final long SMART_COPY_SCHEDULER_APPEND_CHAIN_MERGE_COUNT_DEFAULT =
        10;
    public static final String SMART_COPY_SCHEDULER_ACTION_RETRY_COUNT_KEY =
        "smart.copy.scheduler.retry.count";
    public static final int SMART_COPY_SCHEDULER_ACTION_RETRY_COUNT_DEFAULT =
        3;
    public static final String SMART_COPY_SCHEDULER_DIFF_CACHE_SYNC_THRESHOLD_KEY =
        "smart.copy.scheduler.diff.cache.sync.threshold";
    public static final int SMART_COPY_SCHEDULER_DIFF_CACHE_SYNC_THRESHOLD_DEFAULT =
        500;

    public static final String SMART_FILE_DIFF_MAX_NUM_RECORDS_KEY =
            "smart.file.diff.max.num.records";
    public static final int SMART_FILE_DIFF_MAX_NUM_RECORDS_DEFAULT =
            10000;

    public static final String SMART_MOVER_SCHEDULER_REPORT_FETCH_INTERVAL_MS_KEY =
            "smart.mover.scheduler.storage.report.fetch.interval.ms";
    public static final long SMART_MOVER_SCHEDULER_REPORT_FETCH_INTERVAL_MS_DEFAULT = 2 * 60 * 1000;

    public static final String SMART_SMALL_FILE_METASTORE_INSERT_BATCH_SIZE_KEY =
            "smart.metastore.small-file.insert.batch.size";
    public static final int SMART_SMALL_FILE_METASTORE_INSERT_BATCH_SIZE_DEFAULT = 200;

    // Dispatcher
    public static final String SMART_CMDLET_DISPATCHER_LOG_DISP_RESULT_KEY =
            "smart.cmdlet.dispatcher.log.disp.result";
    public static final boolean SMART_CMDLET_DISPATCHER_LOG_DISP_RESULT_DEFAULT = false;
    public static final String SMART_CMDLET_DISPATCHERS_KEY = "smart.cmdlet.dispatchers";
    public static final int SMART_CMDLET_DISPATCHERS_DEFAULT = 3;
    public static final String SMART_CMDLET_DISPATCHER_LOG_DISP_METRICS_INTERVAL_KEY =
            "smart.cmdlet.dispatcher.log.disp.metrics.interval"; // in ms
    public static final int SMART_CMDLET_DISPATCHER_LOG_DISP_METRICS_INTERVAL_DEFAULT = 5000;

    // Action
    public static final String SMART_ACTION_MOVE_THROTTLE_MB_KEY = "smart.action.move.throttle.mb";
    public static final long SMART_ACTION_MOVE_THROTTLE_MB_DEFAULT = 0L;  // 0 means unlimited
    public static final String SMART_ACTION_COPY_THROTTLE_MB_KEY = "smart.action.copy.throttle.mb";
    public static final long SMART_ACTION_COPY_THROTTLE_MB_DEFAULT = 0L;  // 0 means unlimited
    public static final String SMART_ACTION_EC_THROTTLE_MB_KEY = "smart.action.ec.throttle.mb";
    public static final long SMART_ACTION_EC_THROTTLE_MB_DEFAULT = 0L;
    public static final String SMART_ACTION_LOCAL_EXECUTION_DISABLED_KEY =
            "smart.action.local.execution.disabled";
    public static final boolean SMART_ACTION_LOCAL_EXECUTION_DISABLED_DEFAULT = false;

    // SmartAgent
    public static final String SMART_AGENT_MASTER_PORT_KEY = "smart.agent.master.port";
    public static final int SMART_AGENT_MASTER_PORT_DEFAULT = 7051;
    public static final String SMART_AGENT_PORT_KEY = "smart.agent.port";
    public static final int SMART_AGENT_PORT_DEFAULT = 7048;

    public static final String SMART_AGENT_MASTER_ASK_TIMEOUT_MS_KEY =
            "smart.agent.master.ask.timeout.ms";
    public static final long SMART_AGENT_MASTER_ASK_TIMEOUT_MS_DEFAULT = 5000L;

    /** Do NOT configure the following two options manually. They are set by the boot scripts. **/
    public static final String SMART_AGENT_MASTER_ADDRESS_KEY = "smart.agent.master.address";
    public static final String SMART_AGENT_ADDRESS_KEY = "smart.agent.address";

    // Small File Compact
    public static final String SMART_COMPACT_BATCH_SIZE_KEY =
            "smart.compact.batch.size";
    public static final int SMART_COMPACT_BATCH_SIZE_DEFAULT =
            200;
    public static final String SMART_COMPACT_CONTAINER_FILE_THRESHOLD_MB_KEY =
            "smart.compact.container.file.threshold.mb";
    public static final long SMART_COMPACT_CONTAINER_FILE_THRESHOLD_MB_DEFAULT =
            1024;

    // SmartClient

    // Common
    /**
     * Namespace, access info and other info related to files under these dirs will be ignored.
     * Clients will not report access event of these files to SSM.
     * For more than one directories, they should be separated by ",".
     */
    public static final String SMART_IGNORE_DIRS_KEY = "smart.ignore.dirs";
    /**
     * Namespace, access info and other info only related to files under these dirs will be tackled.
     * For more than one directories, they should be separated by ",".
     */
    public static final String SMART_COVER_DIRS_KEY = "smart.cover.dirs";
    public static final String SMART_WORK_DIR_KEY = "smart.work.dir";
    public static final String SMART_WORK_DIR_DEFAULT = "/system/ssm/";
    public static final String SMART_IGNORED_PATH_TEMPLATES_KEY = "smart.ignore.path.templates";
    public static final String SMART_INTERNAL_PATH_TEMPLATES_KEY = "smart.internal.path.templates";
    public static final String SMART_INTERNAL_PATH_TEMPLATES_DEFAULT =
            ".*/\\..*,.*/__.*,.*_COPYING_.*";

    // Target cluster
    public static final String SMART_STORAGE_INFO_UPDATE_INTERVAL_KEY =
            "smart.storage.info.update.interval";
    public static final int SMART_STORAGE_INFO_UPDATE_INTERVAL_DEFAULT = 60;

    //Status report
    public static final String SMART_STATUS_REPORT_PERIOD_KEY = "smart.status.report.period";
    public static final int SMART_STATUS_REPORT_PERIOD_DEFAULT = 10;
    public static final String SMART_STATUS_REPORT_PERIOD_MULTIPLIER_KEY =
            "smart.status.report.period.multiplier";
    public static final int SMART_STATUS_REPORT_PERIOD_MULTIPLIER_DEFAULT = 50;
    public static final String SMART_STATUS_REPORT_RATIO_KEY = "smart.status.report.ratio";
    public static final double SMART_STATUS_REPORT_RATIO_DEFAULT = 0.2;

    // Compression
    public static final String SMART_COMPRESSION_CODEC = "smart.compression.codec";
    public static final String SMART_COMPRESSION_CODEC_DEFAULT = "Zlib";
    public static final String SMART_COMPRESSION_MAX_SPLIT = "smart.compression.max.split";
    public static final int SMART_COMPRESSION_MAX_SPLIT_DEFAULT = 1000;

    // Enable current report or not in SSM HA mode.
    public static final String SMART_CLIENT_CONCURRENT_REPORT_ENABLED =
            "smart.client.concurrent.report.enabled";
    public static final boolean SMART_CLIENT_CONCURRENT_REPORT_ENABLED_DEFAULT = true;

    public static final String SMART_CLIENT_REPORT_TASKS_TIMEOUT_MS_KEY =
            "smart.client.report.tasks.timeout.ms";
    public static final long SMART_CLIENT_REPORT_TASKS_TIMEOUT_MS_DEFAULT = 2000;

    public static final String SMART_CLIENT_ACTIVE_SERVER_CACHE_PATH_KEY =
            "smart.client.active.server.cache.path";
    public static final String SMART_CLIENT_ACTIVE_SERVER_CACHE_PATH_DEFAULT =
            "/tmp/active_smart_server";

    public static final String SMART_ACTION_CLIENT_CACHE_TTL_KEY =
        "smart.action.client.cache.ttl";
    public static final String SMART_ACTION_CLIENT_CACHE_TTL_DEFAULT = "10m";

  public static final String SMART_ACTION_COPY_TRUNCATE_WAIT_MS_KEY =
      "smart.action.copy.truncate.wait.ms";
  public static final long SMART_ACTION_COPY_TRUNCATE_WAIT_MS_DEFAULT = 100L;

  public static final String ACCESS_EVENT_SOURCE_KEY = "smart.data.file.event.source";
    public static final String ACCESS_EVENT_SOURCE_DEFAULT =
        SmartServerAccessEventSource.class.getName();

    // Metrics
    public static final String SMART_METRICS_ENABLED_KEY =
        "smart.metrics.enabled";
    public static final boolean SMART_METRICS_ENABLED_DEFAULT = true;

    public static final String SMART_METRICS_JMX_ENABLED_KEY =
        "smart.metrics.jmx.enabled";
    public static final boolean SMART_METRICS_JMX_ENABLED_DEFAULT = true;

    public static final String SMART_METRICS_JMX_DOMAIN_KEY =
        "smart.metrics.jmx.domain";
    public static final String SMART_METRICS_JMX_DOMAIN_DEFAULT = "metrics";

    public static final String SMART_METRICS_PROMETHEUS_ENABLED_KEY =
        "smart.metrics.prometheus.enabled";
    public static final boolean SMART_METRICS_PROMETHEUS_ENABLED_DEFAULT = true;

    public static final String SMART_METRICS_DB_QUERIES_ENABLED_KEY =
        "smart.metrics.db.queries.enabled";
    public static final boolean SMART_METRICS_DB_QUERIES_ENABLED_DEFAULT = false;
}
