package com.yb.entity;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

// 使用Component注解装配SpringBean(Controller Service Repository同理)
//
@Component
public class OrderByAnno implements InitializingBean,DisposableBean,BeanPostProcessor {
    /**序号1*/
    public OrderByAnno() {
        System.out.println("OrderByAnno instance is created");
    }

    /**序号2
     * 使用@PostConstruct(初始化逻辑)和@PreDestroy(销毁逻辑)注解
     从Java EE 5规范开始，Servlet中增加了两个影响Servlet生命周期的注解：@PostConstruct和@PreDestroy。
     被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。
     被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
     被@PreDestroy修饰的方法会在服务器卸载Servlet的时候运行，并且只会被服务器调用一次，类似于Servlet的destroy()方法。
     被@PreDestroy修饰的方法会在destroy()方法之后运行，在Servlet被彻底卸载之前。
     */
    @PostConstruct
    public void postConstruct(){
        System.out.println("OrderByAnno postConstruct method invoke");
    }

    /**序号3
     * 实现InitializingBean，该方法即init方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("OrderByAnno afterPropertiesSet method invoke");
    }

    /**序号4*/
    @PreDestroy
    public void preDestroy(){
        System.out.println("OrderByAnno preDestroy method invoke");

    }

    /**序号5
     * 实现DisposableBean，即destroy方法
     */
    @Override
    public void destroy() throws Exception {
        System.out.println("OrderByAnno destroy method invoke");
    }
}
