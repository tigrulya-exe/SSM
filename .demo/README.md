# Demo


```shell
/opt/hive/bin/beeline -u 'jdbc:hive2://localhost:10000/'
```

```hiveql
create database db1;
create table db1.t1(i int);
create table db1.t2(i int);
create table db1.t3(i int);

insert into db1.t1 values (1), (2), (3);
insert into db1.t2 values (4), (5), (6);

DESCRIBE extended db1.t1;
```

```
hms : name matches "db1.*" | hms-sync -dest thrift://target-hive-metastore:9083/ -cascade -nameservice_rename "source target"
```