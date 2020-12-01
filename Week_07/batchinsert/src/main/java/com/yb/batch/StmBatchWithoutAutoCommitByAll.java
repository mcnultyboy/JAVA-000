package com.yb.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.yb.batch.ConnectionInfo.*;

/***
 * 使用statement, 关闭自动提交batch,100w条执行commit
 *
 *
 * @auther yb
 * @date 2020/11/30 10:03
 *
 * 插入结果：
st_time == 1606710602320
no commit
no commit
no commit
no commit
no commit
no commit
no commit
no commit
no commit
no commit
1w inserted! use 1606710603984
...
99w inserted! use 1606710700444
no commit
no commit
no commit
no commit
no commit
no commit
no commit
no commit
no commit
no commit
100w inserted! use 1606710701400
commit
over===============
 100w累计耗时=1606710701400-1606710602320=99080 ms = 99 s

 难道是每条记录的数据太少了导致？
 */

public class StmBatchWithoutAutoCommitByAll {


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // 加载数据库驱动
        Class.forName(JDBC_DRIVER);
        try(
                // 打开数据库连接
                Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
                // 获取statement
                Statement statement = connection.createStatement();
        ) {
            connection.setAutoCommit(false); // 关闭自动提交
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
                statement.addBatch(insert_sql);
                // 每1000条提交一次
                if (i % 1000 == 0){
                    statement.executeBatch();
                    System.out.println("no commit");
                }
                if (++batch_insert_num == 10000){
                    end_time = System.currentTimeMillis();
                    System.out.println(++num + "w inserted! use " + end_time);
                    batch_insert_num = 0; // 重置
                }
            }
            System.out.println("commit");
            connection.commit();
            System.out.println("over===============");

        }
    }
}
