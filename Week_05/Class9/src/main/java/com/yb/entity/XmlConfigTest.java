package com.yb.entity;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlConfigTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        OrderByXml orderByXml = context.getBean(OrderByXml.class);
        ProductByXml productByXml = (ProductByXml)context.getBean("myProduct"); // 使用自定义beanId
        System.out.println(productByXml.getOrderByXml() == orderByXml); // true

    }
}
