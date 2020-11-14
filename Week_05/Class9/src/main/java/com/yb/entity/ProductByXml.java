package com.yb.entity;

public class ProductByXml {
    private OrderByXml orderByXml;

    public ProductByXml() {
        System.out.println("ProductByXml instance created");
    }

    public OrderByXml getOrderByXml() {
        return orderByXml;
    }

    public void setOrderByXml(OrderByXml orderByXml) {
        this.orderByXml = orderByXml;
    }
}
