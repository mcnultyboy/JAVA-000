package com.yb;


import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;

public class Test {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("DIYbean.xml");
        School school = context.getBean(School.class);
        System.out.println(school);
    }
}
