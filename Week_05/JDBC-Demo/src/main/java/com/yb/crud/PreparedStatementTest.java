package com.yb.crud;

import java.sql.*;

public class PreparedStatementTest {
    // JDBC 驱动名 及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/jkb";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PWD = "admin";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // 加载数据库驱动
        Class.forName(JDBC_DRIVER);
        // 获取statement
        PreparedStatement ps = null;
        try(
            // 打开数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
        ) {
            // 关闭自动提交
            connection.setAutoCommit(false);
            String id = "1";
            String name = "zhangsan";
            int age = 18;

            // delete
            String delete_sql = "DELETE FROM t_student";
            ps = connection.prepareStatement(delete_sql);
            int delete_size = ps.executeUpdate();
            System.out.println("delete size = " + delete_size);

            // insert
            String insert_sql = "INSERT INTO t_student (id, name, age) VALUES (?, ?, ?)";
            ps = connection.prepareStatement(insert_sql);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setInt(3, age);
            int insert_size = ps.executeUpdate();
            System.out.println("insert size =" +insert_size);

            // queryOne
            String queryOne_sql = "select * from t_student where id = ?";
            ps = connection.prepareStatement(queryOne_sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                String idOfRs = rs.getString("id");
                String nameOfRs = rs.getString("name");
                String ageOfRs = rs.getString("age");
                System.out.println(idOfRs + "," + nameOfRs + "," +ageOfRs);
            }

            //update
            String update_sql = "update t_student set age = ?";
            ps = connection.prepareStatement(update_sql);
            ps.setInt(1, 20);
            int updateSize = ps.executeUpdate();
            System.out.println("update number = " + updateSize);

            //batch update
            ps = connection.prepareStatement(insert_sql);
            for (int i = 1; i < 20; i++) {
                ps.setString(1, String.valueOf(i));
                ps.setString(2, "lisi" + i);
                ps.setInt(3, i);
                ps.addBatch();
            }
            ps.executeBatch();

            // queryAll
            String queryAll_sql = "select * from t_student";
            ps = connection.prepareStatement(queryAll_sql);
            rs = ps.executeQuery();
            while (rs.next()){
                String idOfRs = rs.getString("id");
                String nameOfRs = rs.getString("name");
                String ageOfRs = rs.getString("age");
                System.out.println(idOfRs + "," + nameOfRs + "," +ageOfRs);
            }
            // 手动提交
            connection.commit();
        } finally {
            ps.close();
        }
    }
}
