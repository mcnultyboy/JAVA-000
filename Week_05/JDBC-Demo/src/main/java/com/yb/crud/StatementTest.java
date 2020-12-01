package com.yb.crud;

import java.sql.*;

public class StatementTest {
    // JDBC 驱动名 及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/jkb";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PWD = "admin";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // 加载数据库驱动
        Class.forName(JDBC_DRIVER);
        try(
            // 打开数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
            // 获取statement
            Statement statement = connection.createStatement()
        ) {
            // delete
            String delete_sql = "DELETE FROM t_student";
            int delete_size = statement.executeUpdate(delete_sql);
            System.out.println("delete size = " + delete_size);

            // insert
            String insert_sql = "INSERT INTO t_student (id, name, age) VALUES (\"1\", \"zhangsan\", 18)";
            statement.executeUpdate(insert_sql);

            // query
            String query_sql = "select * from t_student";
            statement.executeQuery(query_sql);
            // 可以通过statement之后获取rs
            ResultSet rs = statement.getResultSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            System.out.println(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(rsmd.getColumnName(i));
                System.out.println(rsmd.getColumnType(i));
                System.out.println(rsmd.getColumnClassName(i));
            }
            while (rs.next()){
                String id = rs.getString("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.println(id + "," + name + "," +age);
            }

            //update
            String update_sql = "update t_student set age = 20";
            int updateSize = statement.executeUpdate(update_sql);
            System.out.println("update number = " + updateSize);

            // query
            // 也可以通过执行获取
            ResultSet rs2 = statement.executeQuery(query_sql);
            while (rs2.next()){
                String id = rs2.getString("id");
                String name = rs2.getString("name");
                int age = rs2.getInt("age");
                System.out.println(id + "," + name + "," +age);
            }
        }
    }

}
