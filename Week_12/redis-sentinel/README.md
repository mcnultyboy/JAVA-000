# 作业要求

1、（必做）配置redis的主从复制，sentinel高可用，Cluster集群。 提交如下内容到github： 

1）config配置文件， 

2）启动和操作、验证集群下数据读写的命令步骤

# 实现步骤

## 1、主从配置

### 1)准备window的redis环境

​	下载的redis版本为 `3.0.502`，版本过高会导致主从配置失败

### 2)配置文件

​	redis.windows.conf 为默认配置，即6379端口(主)

​	redis.windows6380.conf redis.windows6381.conf(2个从)，如下：

```properties
# 6380
port 6380
bind 127.0.0.1
slaveof 127.0.0.1 6379 # 配置主从
# 6381
port 6381
bind 127.0.0.1
slaveof 127.0.0.1 6379 # 配置主从
```

### 3)启动主从

​	主6379

```
PS E:\Redis-x64-3.0.502> .\redis-server.exe .\redis.windows.conf
...
[15596] 20 Jan 11:34:42.309 # Server started, Redis version 3.0.502
[15596] 20 Jan 11:34:42.309 * The server is now ready to accept connections on port 6379
[15596] 20 Jan 11:35:23.950 * Slave 127.0.0.1:6380 asks for synchronization  # 6380 进行同步
[15596] 20 Jan 11:35:23.950 * Full resync requested by slave 127.0.0.1:6380
[15596] 20 Jan 11:35:23.950 * Starting BGSAVE for SYNC with target: disk
[15596] 20 Jan 11:35:23.955 * Background saving started by pid 18944
[15596] 20 Jan 11:35:24.160 # fork operation complete
[15596] 20 Jan 11:35:24.161 * Background saving terminated with success
[15596] 20 Jan 11:35:24.164 * Synchronization with slave 127.0.0.1:6380 succeeded
[15596] 20 Jan 11:36:05.095 * Slave 127.0.0.1:6381 asks for synchronization
[15596] 20 Jan 11:36:05.095 * Full resync requested by slave 127.0.0.1:6381 # 6381 进行同步
[15596] 20 Jan 11:36:05.096 * Starting BGSAVE for SYNC with target: disk
[15596] 20 Jan 11:36:05.100 * Background saving started by pid 2788
```

​	从6380、6381(类似，没贴)

```
PS E:\Redis-x64-3.0.502> .\redis-server.exe .\redis.windows6380.conf
...
[18120] 20 Jan 11:35:23.947 # Server started, Redis version 3.0.502
[18120] 20 Jan 11:35:23.948 * The server is now ready to accept connections on port 6380
[18120] 20 Jan 11:35:23.948 * Connecting to MASTER 127.0.0.1:6379
[18120] 20 Jan 11:35:23.949 * MASTER <-> SLAVE sync started
[18120] 20 Jan 11:35:23.949 * Non blocking connect for SYNC fired the event.
[18120] 20 Jan 11:35:23.949 * Master replied to PING, replication can continue...
[18120] 20 Jan 11:35:23.950 * Partial resynchronization not possible (no cached master)
[18120] 20 Jan 11:35:23.955 * Full resync from master: c2f28a11572c3ee5e8a435068e7c422ca8fc5868:1
[18120] 20 Jan 11:35:24.164 * MASTER <-> SLAVE sync: receiving 18 bytes from master
[18120] 20 Jan 11:35:24.167 * MASTER <-> SLAVE sync: Flushing old data
[18120] 20 Jan 11:35:24.168 * MASTER <-> SLAVE sync: Loading DB in memory
[18120] 20 Jan 11:35:24.168 * MASTER <-> SLAVE sync: Finished with success
```

### 4)主从测试

​	主6379写入

```
PS E:\Redis-x64-3.0.502> .\redis-cli.exe -h 127.0.0.1 -p 6379
127.0.0.1:6379> info replication
# Replication
role:master
connected_slaves:2
slave0:ip=127.0.0.1,port=6380,state=online,offset=99,lag=0
slave1:ip=127.0.0.1,port=6381,state=online,offset=99,lag=0
master_repl_offset:99
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:2
repl_backlog_histlen:98
127.0.0.1:6379> exit
PS E:\Redis-x64-3.0.502> .\redis-cli.exe -h 127.0.0.1 -p 6379
127.0.0.1:6379> set key1 haha
OK
127.0.0.1:6379> get key1
"haha"
```

​	从6380读取

```
PS E:\Redis-x64-3.0.502> .\redis-cli.exe -h 127.0.0.1 -p 6380
127.0.0.1:6380> dbsize
(integer) 1
127.0.0.1:6380> keys *
1) "key1"
127.0.0.1:6380> info replication
# Replication
role:slave
master_host:127.0.0.1
master_port:6379
master_link_status:up
master_last_io_seconds_ago:9
master_sync_in_progress:0
slave_repl_offset:3053
slave_priority:100
slave_read_only:1
connected_slaves:0
master_repl_offset:0
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0
127.0.0.1:6380>
```

## 2、Redis哨兵配置

Redis-Sentinel(哨兵模式)是Redis官方推荐的高可用性(HA)解决方案，当用Redis做Master-slave的高可用方案时，假如master宕机了，Redis本身(包括它的很多客户端)都没有实现自动进行主备切换，而Redis-sentinel本身也是一个独立运行的进程，它能监控多个master-slave集群，发现master宕机后能进行自懂切换。

### 1)创建sentinel配置文件(3个为集群)

​	sentinel26379.conf sentinel26380.conf sentinel26381.conf ，内容如下：

```
port 26379
sentinel monitor mymaster 127.0.0.1 6380 2  # 哨兵的名字，监听的主的ip port,选举决定的哨兵数量，一般配置为>1/2，少数服从多数
sentinel down-after-milliseconds mymaster 10000
sentinel failover-timeout mymaster 15000

port 26380
sentinel monitor mymaster 127.0.0.1 6380 2
sentinel down-after-milliseconds mymaster 10000
sentinel failover-timeout mymaster 15000

port 26381
sentinel monitor mymaster 127.0.0.1 6380 2
sentinel down-after-milliseconds mymaster 10000
sentinel failover-timeout mymaster 15000
```

### 2)启动哨兵集群

```
./redis-server.exe sentinel26379.conf --sentinel
./redis-server.exe sentinel26380.conf --sentinel
./redis-server.exe sentinel26381.conf --sentinel
...26379如下
[17684] 20 Jan 12:33:41.361 # Sentinel runid is dc11b1acf4664c6f42f18d18ffb9bdd10513895a
[17684] 20 Jan 12:33:41.362 # +monitor master mymaster 127.0.0.1 6379 quorum 2
[17684] 20 Jan 12:33:42.369 * +slave slave 127.0.0.1:6380 127.0.0.1 6380 @ mymaster 127.0.0.1 6379 # 获取slave1信息
[17684] 20 Jan 12:33:42.370 * +slave slave 127.0.0.1:6381 127.0.0.1 6381 @ mymaster 127.0.0.1 6379 # 获取slave2信息
[17684] 20 Jan 12:34:31.210 * +sentinel sentinel 127.0.0.1:26380 127.0.0.1 26380 @ mymaster 127.0.0.1 6379 # 扫描到其他sentinel信息
[17684] 20 Jan 12:34:53.393 * +sentinel sentinel 127.0.0.1:26381 127.0.0.1 26381 @ mymaster 127.0.0.1 6379
```

### 3)模拟主down之后，重新选举

```
# 1.关闭主6379
[15596] 20 Jan 12:35:18.678 # User requested shutdown...
[15596] 20 Jan 12:35:18.678 * Saving the final RDB snapshot before exiting.
[15596] 20 Jan 12:35:18.742 * DB saved on disk
[15596] 20 Jan 12:35:18.743 # Redis is now ready to exit, bye bye...

# 2.查看sentinel选举过程(注意，中间会耗时)
[17684] 20 Jan 12:35:28.868 # +new-epoch 1
[17684] 20 Jan 12:35:28.869 # +vote-for-leader e87ff8198bc92b51a35520f5ed68eda240398eb1 1 # 该sentinel进行投票
[17684] 20 Jan 12:35:28.896 # +sdown master mymaster 127.0.0.1 6379 # 该sentinel主观认为master 已经 down
[17684] 20 Jan 12:35:28.990 # +odown master mymaster 127.0.0.1 6379 #quorum 3/2 # 经过投票，多数sentinel 客观认为master 已经 down
[17684] 20 Jan 12:35:28.990 # Next failover delay: I will not start a failover before Wed Jan 20 12:35:59 2021
[17684] 20 Jan 12:35:29.956 # +config-update-from sentinel 127.0.0.1:26381 127.0.0.1 26381 @ mymaster 127.0.0.1 6379
[17684] 20 Jan 12:35:29.956 # +switch-master mymaster 127.0.0.1 6379 127.0.0.1 6380 # 选举 6380 为新的master
[17684] 20 Jan 12:35:29.957 * +slave slave 127.0.0.1:6381 127.0.0.1 6381 @ mymaster 127.0.0.1 6380 # 配置新的主从关系
[17684] 20 Jan 12:35:29.957 * +slave slave 127.0.0.1:6379 127.0.0.1 6379 @ mymaster 127.0.0.1 6380
[17684] 20 Jan 12:35:40.004 # +sdown slave 127.0.0.1:6379 127.0.0.1 6379 @ mymaster 127.0.0.1 6380

# 3.查看6380的info
127.0.0.1:6380> info replication
# Replication
role:master # 已经为master，切换成功
connected_slaves:1
slave0:ip=127.0.0.1,port=6381,state=online,offset=1221,lag=1 # 6379已下线，只有6381这个slave
master_repl_offset:1221
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:2
repl_backlog_histlen:1220

# 4.重新启动6379
.\redis-server.exe .\redis.windows.conf
...
[292] 20 Jan 13:28:12.196 # Server started, Redis version 3.0.502
[292] 20 Jan 13:28:12.196 * DB loaded from disk: 0.000 seconds
[292] 20 Jan 13:28:12.196 * The server is now ready to accept connections on port 6379
[292] 20 Jan 13:28:22.307 * SLAVE OF 127.0.0.1:6380 enabled (user request from 'id=3 addr=127.0.0.1:64941 fd=8 name=sentinel-e87ff819-cmd age=10 idle=0 flags=x db=0 sub=0 psub=0 multi=3 qbuf=0 qbuf-free=32768 obl=36 oll=0 omem=0 events=rw cmd=exec')
[292] 20 Jan 13:28:22.310 # CONFIG REWRITE executed with success.
[292] 20 Jan 13:28:23.331 * Connecting to MASTER 127.0.0.1:6380 # 自己已经是slave，需要连接master进行同步
[292] 20 Jan 13:28:23.332 * MASTER <-> SLAVE sync started
[292] 20 Jan 13:28:23.332 * Non blocking connect for SYNC fired the event.
[292] 20 Jan 13:28:23.332 * Master replied to PING, replication can continue...
[292] 20 Jan 13:28:23.333 * Partial resynchronization not possible (no cached master)
[292] 20 Jan 13:28:23.347 * Full resync from master: 35669c87ff666c5b2b7ae566f38e79910994ac07:615299
[292] 20 Jan 13:28:23.525 * MASTER <-> SLAVE sync: receiving 40 bytes from master
[292] 20 Jan 13:28:23.528 * MASTER <-> SLAVE sync: Flushing old data
[292] 20 Jan 13:28:23.528 * MASTER <-> SLAVE sync: Loading DB in memory
[292] 20 Jan 13:28:23.529 * MASTER <-> SLAVE sync: Finished with success


# 5.查看6380的info
127.0.0.1:6380> info replication
# Replication
role:master
connected_slaves:2
slave0:ip=127.0.0.1,port=6381,state=online,offset=631394,lag=1
slave1:ip=127.0.0.1,port=6379,state=online,offset=631261,lag=1  # 6379已上线，并称为slave
master_repl_offset:631394
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:2
repl_backlog_histlen:631393
```

## 3、Cluster配置

### 1)安装ruby环境

​	双击下载的“rubyinstaller-2.7.2-1-x64.exe”安装即可，同样，为了操作方便，也是建议安装在盘符根目录下，如： C:\Ruby27-x64 ，安装会默认把ruby添加到path环境变量





## 4、Reids分片

### 1)分片和扩容	

​	Hash槽共有**16384(2的14次方)**个槽，每台服务器分管一部分。假设有三台服务器，第一台服务器负责【0，5460】这个范围，第二台服务器负责【5461，10992】这个范围，第三台服务器负责【10923,16383】这个范围。

​	采用**CRC16**算法先对key产生一个整数值，再对16384求余数，即CRC16（key）%16384。也就是通过Hash槽分发数据。

​	**增加新的服务器需要对key进行迁移，即从一台服务器搬到另一台服务器**。比如增加服务器4，那就会从服务器1、服务器2、服务器3负责的槽中各自分出一些交给服务器4管理，对应的数据也要搬过去。保证数据平均分布到所有服务器上。如下图（Hash槽重新分配）所示。也就是每一台服务器和hash槽有一个映射关系。比如 取数据的时候根据CRC16（key）%16384=0的值对应槽，从而对应服务器4

### 2) 查看集群分片现状

​	命令：`cluster slots` 会展示出所有的节点的分片逻辑

​	cluster默认需要使用偶数的节点，一半作为master 一半作为slave。相当于cluster内部也是使用了主从和sentinel。





# 参考记录

[主从、哨兵、cluster配置]( https://blog.csdn.net/baidu_27627251/article/details/112143714)
[主从、哨兵、cluster的区别](https://blog.csdn.net/angjunqiang/article/details/81190562) 



