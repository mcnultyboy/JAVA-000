package com.yb.batch;

import java.sql.*;

import static com.yb.batch.ConnectionInfo.*;

/***
 * 使用PrepareStatement, 关闭自动提交batch,每1000条commit一次
 *
 *
 * @auther yb
 * @date 2020/11/30 10:03
 *
 * 插入结果：
st_time == 1606713123967
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
1w inserted! use 1606713128747
...
99w inserted! use 1606713356947
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
100w inserted! use 1606713359364
over===============
 100w累计耗时=1606713359364-1606713123967=235397 ms = 235 s

 */

public class PSBatchWithoutAutoCommitBy1000 {


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // 加载数据库驱动
        Class.forName(JDBC_DRIVER);
        try(
                // 打开数据库连接
                Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
        ) {
            connection.setAutoCommit(false); // 关闭自动提交
            // 获取 ps
            String insert_sql = "insert into t_order VALUES (?,?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(insert_sql);
            int batch_insert_num = 0;
            int num = 0; // 批次数
            long st_time = System.currentTimeMillis();
            long end_time = 0L;
            System.out.println("st_time == " + st_time);
            // 设置公共参数
            ps.setLong(2, 111);
            ps.setLong(3, 123);
            ps.setLong(4, 1);
            ps.setString(5, "1");
            for (int i = 1; i <= 1000000; i++) {
                ps.setLong(1, i);
                ps.setLong(6, System.currentTimeMillis());
                ps.setLong(7, System.currentTimeMillis());
                ps.addBatch();
                // 每1000条提交一次
                if (i % 1000 == 0){
                    ps.executeBatch();
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
            ps.close();

        }
    }
}
