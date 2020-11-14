package com.yb.jdkProxy;

import java.lang.reflect.Proxy;

/***
 * proxy测试类
 *
 * @auther yb
 * @date 2020/11/13 20:18
 */
public class ProxyTest {
    public static void main(String[] args) {
        // 获取被代理类实例
        Student zhangsan = new Student("zhangsan");

        // 创建handler，需要将被代理类实例注入
        StuInvocationHandler<Student> handler = new StuInvocationHandler<>(zhangsan);

        // 创建proxy
        Person proxy = (Person)Proxy.newProxyInstance(Student.class.getClassLoader(),
                new Class[]{Person.class} /**此处需要被代理类的父接口clz*/,
                handler);
        // 执行行为
        System.out.println("start doProxy");
        proxy.eat("banana");
        proxy.run();
    }
}
