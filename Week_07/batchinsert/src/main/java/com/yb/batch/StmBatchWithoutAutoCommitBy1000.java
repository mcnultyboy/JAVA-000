package com.yb.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.yb.batch.ConnectionInfo.*;

/***
 * 使用statement, 关闭自动提交batch,每1000条commit一次
 *
 *
 * @auther yb
 * @date 2020/11/30 10:03
 *
 * 插入结果：
st_time == 1606709192412
commit
commit
commit
commit
commit
commit
commit
commit
commit
commit
1w inserted! use 1606709195211
...
99w inserted! use 1606709347909
commit
commit
commit
commit
commit
commit
commit
commit
commit
commit
100w inserted! use 1606709349310
over===============
 100w累计耗时=1606709349310-1606709192412=156898 ms = 156 s
 */

public class StmBatchWithoutAutoCommitBy1000 {


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
                    connection.commit();
                    System.out.println("commit");
                }
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
