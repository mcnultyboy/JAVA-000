# 周四作业
## 1.必做题，测试mysql的批量插入
### 1.作业位置 batch-insert project
### 2.操作步骤
1. 创建表结构<br>
```sql
CREATE TABLE t_order (
id int(10) NOT NULL,
cst_id int(10) NOT NULL COMMENT '客户id',
prd_id int(10) NOT NULL COMMENT '商品id',
prd_num tinyint(3) NOT NULL COMMENT '商品数量',
status char(1) NOT NULL COMMENT '商品状态 1-购买 2-完成 3-退货',
create_time bigint(20) NOT NULL,
update_time bigint(20) DEFAULT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
2. `StmOneByOne` <br>
* 使用statement顺序插入,每1w条耗时6分钟，太慢了，中断测试<br>
3. `StmBatchWithAutoCommitBy1000`<br>
* 使用使用statement ,设置为自动提交batch,每1000条 executeBatch() 一次<br>
* 总结：executeBatch()没有按照批次commit，依然是每条commit一次<br>
4. `StmBatchWithoutAutoCommitBy1000` <br>
* 使用statement, 关闭自动提交batch,每1000条commit一次,耗时156s<br>
* 由于是批量提交，所以性能比OneByOne提交快很多<br>
5. `StmBatchWithoutAutoCommitByAll` <br>
* 使用statement, 关闭自动提交batch,100w条执行commit,耗时99s<br>
* question:一次性提交比分批次提交快？难道是每条数据太小的原因????<br>
6. `PSBatchWithoutAutoCommitBy1000`<br>
* 使用PrepareStatement, 关闭自动提交batch,每1000条commit一次,耗时235s<br>
* 总结：比Statement耗时多，是因为数据简单，stm没有考虑安全问题，拼接字符串比较快？？？<br>
7. `PSBatchWithoutAutoCommitBy1000AndConcurrent`<br>
* 使用PrepareStatement与多线程, 关闭自动提交batch,每1000条commit一次，耗时53s<br>
* 使用了10个线程，每个线程处理10w条数据<br>
* 生产上需要注意数据的拆分问题<br>
8. 使用存储过程插入数据，没有关闭索引<br>
* 创建存储过程<br>
```sql
CREATE DEFINER=root@localhost PROCEDURE batchInsert()
begin
declare i int;
set i=0;
SELECT now();
while i <= 1000000 do
insert into t_order values (i,i,i,1,'1',123456789,null);
if MOD(i,1000)=0 THEN
COMMIT;
START TRANSACTION;
END IF;
set i=i+1;
end while;
COMMIT;
SELECT NOW();
end
```
* call batchInsert();耗时 66s<br>
* 果然，还是数据库自带命令速度最快。<br>
9. 使用存储过程插入数据，关闭索引<br>
```mysql
mysql> alter table t_order disable keys;
Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> call batchInsert();
+---------------------+
| now()               |
+---------------------+
| 2020-12-01 15:45:29 |
+---------------------+
1 row in set (0.00 sec)

+---------------------+
| NOW()               |
+---------------------+
| 2020-12-01 15:46:34 |
+---------------------+
1 row in set (1 min 5.22 sec)

Query OK, 0 rows affected (1 min 5.22 sec)
```
* 耗时65s，关不关都没有影响？难道是用存储过程批量插入时默认关闭索引？<br>
9. 1 关闭索引，使用java程序在跑批<br>
* 时间也没有缩短。。。<br>
* 难道是聚集索引不能关闭？？<br>
9. 2 为了验证是否为聚集索引不能关掉，则添加普通索引普通索引，并重新跑批，在跑批后查询表空间及索引大小<br>
* KEY `index1` (`cst_id`,`prd_id`) USING BTREE<br>
* 再次跑批<br>
```mysql
mysql> truncate table t_order;
Query OK, 0 rows affected (0.26 sec)

mysql> SELECT TABLE_NAME AS '表名',
-> CONCAT(ROUND(TABLE_ROWS/10000, 2), ' 万行') AS '行数',
-> CONCAT(ROUND(DATA_LENGTH/(1024*1024*1024), 2), ' GB') AS '表空间',
-> CONCAT(ROUND(INDEX_LENGTH/(1024*1024*1024), 2), ' GB') AS '索引空间',
-> CONCAT(ROUND((DATA_LENGTH+INDEX_LENGTH)/(1024*1024*1024),2),' GB') AS'总空间'
-> FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'jkb' AND TABLE_NAME = 't_order' ORDER BY TABLE_ROWS DESC;
+---------+-----------+---------+----------+---------+
| 表名    | 行数      | 表空间  | 索引空间 | 总空间  |
+---------+-----------+---------+----------+---------+
| t_order | 0.00 万行 | 0.00 GB | 0.00 GB  | 0.00 GB |
+---------+-----------+---------+----------+---------+
1 row in set (0.04 sec)

mysql> alter table t_order disable keys;
Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> call batchInsert();
+---------------------+
| now()               |
+---------------------+
| 2020-12-01 16:23:13 |
+---------------------+
1 row in set (0.00 sec)

+---------------------+
| NOW()               |
+---------------------+
| 2020-12-01 16:24:23 |
+---------------------+
1 row in set (1 min 10.02 sec)

Query OK, 0 rows affected (1 min 10.03 sec)

mysql> SELECT TABLE_NAME AS '表名',
-> CONCAT(ROUND(TABLE_ROWS/10000, 2), ' 万行') AS '行数',
-> CONCAT(ROUND(DATA_LENGTH/(1024*1024*1024), 2), ' GB') AS '表空间',
-> CONCAT(ROUND(INDEX_LENGTH/(1024*1024*1024), 2), ' GB') AS '索引空间',
-> CONCAT(ROUND((DATA_LENGTH+INDEX_LENGTH)/(1024*1024*1024),2),' GB') AS'总空间'
-> FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'jkb' AND TABLE_NAME = 't_order' ORDER BY TABLE_ROWS DESC;
+---------+------------+---------+----------+---------+
| 表名    | 行数       | 表空间  | 索引空间 | 总空间  |
+---------+------------+---------+----------+---------+
| t_order | 99.75 万行 | 0.04 GB | 0.02 GB  | 0.06 GB |
+---------+------------+---------+----------+---------+
1 row in set (0.00 sec)
```
* question??为什么关闭了索引之后，依然有在创建索引？难道是关闭的方式不对？<br>




