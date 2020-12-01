package com.yb.batch;

public interface ConnectionInfo {
    // JDBC 驱动名 及数据库 URL
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://localhost:3306/jkb";

    // 数据库的用户名与密码，需要根据自己的设置
    String USER = "root";
    String PWD = "admin";
}
