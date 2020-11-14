package com.yb.jdkProxy;

/***
 * 被代理类父接口，JdkProxy需要有父接口，代理类模拟为接口的实现类
 *
 * @auther yb
 * @date 2020/11/13 20:04
 */
public interface Person {
    public void eat(String food);

    public void run();
}
