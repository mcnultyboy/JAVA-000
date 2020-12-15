package com.yb.MultiDataSource1.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/***
 * 向DynamicDataSource中注入数据源
 *
 * @auther yb
 * @date 2020/12/3 20:07
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    // 由于DruidDataSource默认使用spring.datasource.druid，所以必须自定义指定配置
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
    public DataSource master(){
        // 由于配置得是Druid连接池，所以要使用DruidDataSource
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setName("master");
        return dataSource;
    }


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid.slave1")
    public DataSource slave1(){
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setName("slave1");
        return dataSource;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid.slave2")
    public DataSource slave2(){
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setName("slave2");
        return dataSource;
    }


    @Bean(name = "dynamicDataSource")
    @Primary // 此时有多个数据源，autowired时，此bean优先
    public DataSource dynamicDataSource(@Autowired @Qualifier("master")DataSource master, @Autowired @Qualifier("slave1")DataSource slave1, @Autowired @Qualifier("slave2")DataSource slave2) {
        // 数据源映射，DynamicDataSource 根据ThreadLocal中的key定位数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER.name(), master);
        targetDataSources.put(DataSourceType.SLAVE1.name(), slave1);
        targetDataSources.put(DataSourceType.SLAVE2.name(), slave2);
        return new DynamicDataSource(master, targetDataSources);
    }
    // 事务
    @Bean
    public PlatformTransactionManager txManager(DataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }


}
