package com.yb.MultiDataSource1.datasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceSwith { // dataSource 类型切换注解，作用在method上
    // 默认使用master
    DataSourceType value() default DataSourceType.MASTER;
}
