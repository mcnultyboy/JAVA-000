package com.yb.batch;

import java.sql.*;

import static com.yb.batch.ConnectionInfo.*;

/***
 * 使用statement 顺序插入
 *
 *
 * @auther yb
 * @date 2020/11/30 10:03
 *
 * 插入结果：
st_time == 1606704290349
1w inserted! use 1606704657222
2w inserted! use 1606705018477
3w inserted! use 1606705378994
4w inserted! use 1606705742029
 每1w条耗时6分钟，太慢了，100w条预计耗时600分钟，约10个小时。

CREATE TABLE `t_order` (
`id` int(10) NOT NULL,
`cst_id` int(10) NOT NULL COMMENT '客户id',
`prd_id` int(10) NOT NULL COMMENT '商品id',
`prd_num` tinyint(3) NOT NULL COMMENT '商品数量',
`status` char(1) NOT NULL COMMENT '商品状态 1-购买 2-完成 3-退货',
`create_time` bigint(20) NOT NULL,
`update_time` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 使用存储过程插入100w条：
 1.创建存储过程
CREATE DEFINER=`root`@`localhost` PROCEDURE `batchInsert`()
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
 1.没有关闭索引插入：66s
 2.关闭索引插入  65s
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

  没有差别，难道是用存储过程批量插入时默认关闭索引？
 3.关闭索引，使用java程序在跑批
  时间也没有缩短。。。
  难道是聚集索引不能关闭？？
 3.为了验证是否为聚集索引不能关掉，则添加普通索引普通索引，并重新跑批，在跑批后查询表空间及索引大小
--创建索引
CREATE TABLE `t_order` (
`id` int(10) NOT NULL,
`cst_id` int(10) NOT NULL COMMENT '客户id',
`prd_id` int(10) NOT NULL COMMENT '商品id',
`prd_num` tinyint(3) NOT NULL COMMENT '商品数量',
`status` char(1) NOT NULL COMMENT '商品状态 1-购买 2-完成 3-退货',
`create_time` bigint(20) NOT NULL,
`update_time` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `index1` (`cst_id`,`prd_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--再次跑批



 */

public class StmOneByOne {


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // 加载数据库驱动
        Class.forName(JDBC_DRIVER);
        try(
                // 打开数据库连接
                Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
                // 获取statement
                Statement statement = connection.createStatement();
        ) {
            int batch_insert_num = 0;
            int num = 0; // 批次数
            long st_time = System.currentTimeMillis();
            long end_time = 0L;
            System.out.println("st_time == " + st_time);
            String insert_sql = null;
            for (int i = 1; i <= 1000000; i++) {
                insert_sql = "insert into t_order values (" +
                        i +
                        ",111,123,1,1," +
                        System.currentTimeMillis() +
                        ",NULL)";
                statement.executeUpdate(insert_sql);
                if (++batch_insert_num == 10000){
                    end_time = System.currentTimeMillis();
                    System.out.println(++num + "w inserted! use " + end_time);
                    batch_insert_num = 0; // 重置
                }
            }
            System.out.println("over===============");

        }
    }
}
