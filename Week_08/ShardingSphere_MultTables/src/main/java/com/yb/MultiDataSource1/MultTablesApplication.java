package com.yb.MultiDataSource1;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 排除DataSourceAutoConfiguration，避免循环依赖，因为它会读取application.properties文件的spring.datasource.*属性并自动配置单数据源。
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
public class MultTablesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultTablesApplication.class, args);
	}

}
