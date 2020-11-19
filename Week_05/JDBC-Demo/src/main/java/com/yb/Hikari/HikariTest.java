package com.yb.Hikari;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HikariTest {
    public static void main(String[] args) throws Exception {
        String id = "1";
        String name = "zhangsan";
        int age = 18;
        try(HikariDataSource dataSource = DataSourceFactory.getInstance();
            Connection connection = dataSource.getConnection();) {

//            for (int i = 0; i < 10; i++) {
//                Connection c = dataSource.getConnection();
//                System.out.println(c);
//            }
            boolean valid = connection.isValid(10);
            System.out.println("valid == " + valid);

            // delete before
            String delete_sql = "DELETE FROM t_student";
            PreparedStatement ps = connection.prepareStatement(delete_sql);
            int delete_size = ps.executeUpdate();
            System.out.println("delete size = " + delete_size);

            // insert
            String insert_sql = "INSERT INTO t_student (id, name, age) VALUES (?, ?, ?)";
            ps = connection.prepareStatement(insert_sql);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setInt(3, age);
            int insert_size = ps.executeUpdate();
            System.out.println("insert size =" + insert_size);

            // queryAll
            String queryAll_sql = "SELECT * FROM t_student";
            ps = connection.prepareStatement(queryAll_sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String idOfRs = rs.getString("id");
                String nameOfRs = rs.getString("name");
                String ageOfRs = rs.getString("age");
                System.out.println(idOfRs + "," + nameOfRs + "," + ageOfRs);
            }

            // delete after
            ps = connection.prepareStatement(delete_sql);
            delete_size = ps.executeUpdate();
            System.out.println("delete size = " + delete_size);

            dataSource.close();
        }

    }
}
