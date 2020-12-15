package com.yb.MultiDataSource1.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/***
 * 类作用
 * AbstractRoutingDataSource.getConnection()时，调用determineTargetDataSource()，
 * 首先检查 resolvedDataSources 是否有值
 * 再调用determineCurrentLookupKey，根据key，到 resolvedDataSources中获取dataSource
 * 所以，在初始化DynamicDataSource时，需要设置 resolvedDataSources的值
 *
 * @auther yb
 * @date 2020/12/3 19:54
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        // 设置默认数据源
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        // 设置多数据源
        super.setTargetDataSources(targetDataSources);
        // 完成 targetDataSources 到 resolvedDataSources的转换，方便父类使用
        super.afterPropertiesSet();
    }


    /**
     *
     * 获取当前线程datasource的类型
     *
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }
}
