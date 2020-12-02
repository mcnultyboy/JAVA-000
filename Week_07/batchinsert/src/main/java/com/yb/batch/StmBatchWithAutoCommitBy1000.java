package com.yb.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.yb.batch.ConnectionInfo.*;

/***
 * 使用使用statement ,设置为自动提交batch,每1000条executeBatch()一次
 *
 *
 * @auther yb
 * @date 2020/11/30 10:03
 *
 * 插入结果：
 非常慢，batch没有生效。
 没有生效的原因是把conn设置为自动提交，每次执行addBatch就会自动提交一次，与OneByOne没有区别。
 总结：
    需要把conn设置为手动提交！！！！！！

 */

public class StmBatchWithAutoCommitBy1000 {


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // 加载数据库驱动
        Class.forName(JDBC_DRIVER);
        try(
                // 打开数据库连接
                Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
                // 获取statement
                Statement statement = connection.createStatement();
        ) {
            connection.setAutoCommit(true); // 开启自动提交
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
                statement.addBatch(insert_sql); // addBatch每次执行会commit一次，这是为什么？导致很慢，batch没有生效
                // 每1000条提交一次
                if (i % 1000 == 0){
                    statement.executeBatch();
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
