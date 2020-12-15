package com.yb.MultiDataSource1.datasource;

public enum  DataSourceType {
    MASTER,
    SLAVE1,
    SLAVE2,
    SLAVE; // 表示动态路由到任意slave数据库
}
