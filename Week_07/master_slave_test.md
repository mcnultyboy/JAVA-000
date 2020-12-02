### 主从复制演示

[TOC]

#### 准备两个MySQL服务实例

> windows上可以用压缩版本，例如mysql-5.7.31-winx64.zip，解压文件夹再复制一份，添加my.ini配置文件。假设一个叫mysql-5.7.31-winx64，一个叫mysql-5.7.31-winx64-2，以为分别配置其为主和从。
>
> Mac和Linux环境，自己想办法，也可以用docker

#### 修改主mysql-5.7.31-winx64的my.ini

```
[mysqld]
basedir=./
datadir=./data
port=3306
server_id=1

sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES 
log_bin=mysql-bin
binlog-format=Row
```



#### 修改从mysql-5.7.31-winx64-2的my.ini

```
[mysqld]
basedir=./
datadir=./data
port=3316
server_id=2

sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES 
log_bin=mysql-bin
binlog-format=Row  #指定binlog的格式row
```



#### 初始化和启动数据库，不需要install,也不需要注册服务启动服务

1.空数据库需要执行mysqld --initialize-insecure 进行初始化(insecure表示无密码进入)。安装路径bin下会默认生成一个data文件夹。
2.分别启动主和从，在命令行下直接执行.\mysqld或start mysqld命令即可。
3.如果出现了问题，则msqld --remove 删除掉服务，再把data文件夹也删除，然后重新执行1 4 步
PS E:\mysql\mysql-5.7.31-winx64\bin> .\mysqld --remove
Service successfully removed.
PS E:\mysql\mysql-5.7.31-winx64\bin> .\mysqld # 此时，当前终端阻塞，需要另外开一个终端

 【解决办法】：
（1）可以输入mysqld --console 启动 查看启动的报错信息，博主的报错信息是3306 端口已经被另一个服务占用，该次启动无法进行。这是问题的关键所在。




#### 配置主节点

mysql命令登录到主节点：mysql -uroot -P3306


```
mysql> show variables like '%port%';
+--------------------------+-------+
| Variable_name            | Value |
+--------------------------+-------+
| innodb_support_xa        | ON    |
| large_files_support      | ON    |
| port                     | 3306  |
| report_host              |       |
| report_password          |       |
| report_port              | 3306  |
| report_user              |       |
| require_secure_transport | OFF   |
+--------------------------+-------+
8 rows in set, 1 warning (0.00 sec)


mysql> CREATE USER 'repl'@'%' IDENTIFIED BY '123456';
Query OK, 0 rows affected (0.11 sec)

mysql> GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%'; #分配权限
Query OK, 0 rows affected (0.12 sec)

mysql> flush privileges; #刷新权限
Query OK, 0 rows affected (0.10 sec)

mysql> show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000003 |      305 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
```

创建数据库：
```
create schema db;
mysql> show schemas;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| db                 |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.00 sec)
```

#### 配置从节点

mysql命令登录到从节点：mysql -uroot -P3316

```
CHANGE MASTER TO
    MASTER_HOST='localhost',  
    MASTER_PORT = 3306,
    MASTER_USER='repl',      
    MASTER_PASSWORD='123456',   
    MASTER_LOG_FILE='mysql-bin.000004',
    MASTER_LOG_POS=2064;
    
    //MASTER_AUTO_POSITION = 1;
```

创建数据库：create schema db;



#### 验证操作

在主库执行：

```
mysql> use db
Database changed
mysql> create table t1(id int);
Query OK, 0 rows affected (0.17 sec)

mysql>
mysql>
mysql> insert into t1(id) values(1),(2);
Query OK, 2 rows affected (0.04 sec)
```



在从库查看数据同步情况
1. 没有数据
查看从状态
```
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: localhost
                  Master_User: repl
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000004
          Read_Master_Log_Pos: 1712
               Relay_Log_File: PC201608132211-relay-bin.000004
                Relay_Log_Pos: 320
        Relay_Master_Log_File: mysql-bin.000004
             Slave_IO_Running: Yes
            Slave_SQL_Running: No
         	  Replicate_Do_DB:
          Replicate_Ignore_DB:
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 1146
                   Last_Error: Error 'Unknown error 1146' on query. Default database: 'db'. Query: 'create table t2 like t1'
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 1310
              Relay_Log_Space: 938
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: NULL
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 1146
               Last_SQL_Error: Error 'Unknown error 1146' on query. Default database: 'db'. Query: 'create table t2 like t1'
```
原因：Slave_SQL_Running 进程没有启动。
说明：
Slave_IO_Running：连接到主库，并读取主库的日志到本地，生成本地日志文件
Slave_SQL_Running:读取本地日志文件，并执行日志里的SQL命令。
解决办法：
```
mysql> stop slave;
Query OK, 0 rows affected (0.04 sec)

mysql> SET GLOBAL SQL_SLAVE_SKIP_COUNTER=1; 
Query OK, 0 rows affected (0.00 sec)

mysql> start slave;
Query OK, 0 rows affected (0.03 sec)
```
再次查看，2个进程都是Yes。

2. 再次查看数据同步情况
还是没有数据。此时发现还有一个问题：
```
Last_SQL_Error: Error 'Unknown error 1146' on query. Default database: 'db'. Query: 'create table t2 like t1'
```
说明从在执行已经同步过来的sql文件时出错。因为从库中没有t1表，所以报错。
解决办法：停止同步，在主中删除t1 t2，查看主的当前位置；从中设置最新的当前位置，打开同步。

```
mysql> use db
Database changed
mysql>
mysql>
mysql> show tables;
+--------------+
| Tables_in_db |
+--------------+
| t1           |
+--------------+
1 row in set (0.00 sec)

mysql>
mysql>
mysql> select * from t1;
+------+
| id   |
+------+
|    1 |
|    2 |
+------+
2 rows in set (0.00 sec)
```



#### 查看命令

可以通过show master status\G, show slave status\G 查看状态

可以能改过stop slave; start slave;来停止复制。



#### 其他

GTID与复制：

https://blog.51cto.com/13540167/2086045

https://www.cnblogs.com/zping/p/10789151.html

半同步复制：

https://www.cnblogs.com/zero-gg/p/9057092.html

组复制：

https://www.cnblogs.com/lvxqxin/p/9407080.html