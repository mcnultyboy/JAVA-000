package com.yb.MultiDataSource1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//指定aop事务执行顺序，已保证在切换数据源的后面
@EnableTransactionManagement(order = 2)
// 排除DataSourceAutoConfiguration，避免循环依赖，因为它会读取application.properties文件的spring.datasource.*属性并自动配置单数据源。
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MultiDataSource1Application {

	public static void main(String[] args) {
		SpringApplication.run(MultiDataSource1Application.class, args);
	}

}
