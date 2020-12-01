package com.yb.batch;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceFactory {
    private static HikariDataSource dataSource;

    private DataSourceFactory() {
    }

    public static HikariDataSource getInstance(){
        if (dataSource == null){
            synchronized (DataSourceFactory.class){
                if (dataSource == null){
                    dataSource = createDataSource();
                }
                return dataSource;
            }
        }
        return dataSource;
    }

    private static HikariDataSource createDataSource() {
        //获取db.properties配置
        // CloClassLoader().getResourceAsStream(path),path从根目录开始查找，所以不能以/为前缀
        // class.getResourceAsStream(path)，当不带/时，从当前class的相对位置开始查找，所以一般需要以/为前缀
        InputStream is = DataSourceFactory.class.getResourceAsStream("/db.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            // todo
        }
        String db_max_connection = properties.getProperty("db_max_connection", "5");
        String db_driver_name = properties.getProperty("db_driver_name");
        String db_url = properties.getProperty("db_url");
        String db_user = properties.getProperty("db_user", "root");
        String db_pwd = properties.getProperty("db_pwd", "admin");

        // 创建Hikari config
        HikariConfig config = new HikariConfig();
        config.setPoolName("testPool");
        config.setMaximumPoolSize(Integer.valueOf(db_max_connection));
        config.setDriverClassName(db_driver_name);
        config.setJdbcUrl(db_url); // jdbc:mysql://ip:port/db_name
        config.setUsername(db_user);
        config.setPassword(db_pwd);

        // 创建Hikari连接池
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        return hikariDataSource;
    }

}
