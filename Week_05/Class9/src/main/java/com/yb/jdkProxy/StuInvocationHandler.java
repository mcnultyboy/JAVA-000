package com.yb.jdkProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/***
 * 类作用
 *
 * @auther yb
 * @date 2020/11/13 20:03
 */
public class StuInvocationHandler<T> implements InvocationHandler {
    T target;

    public StuInvocationHandler(T target) {
        this.target = target;
    }

    /**f
     *
     * @param proxy 动态代理类对象
     * @param method 被代理类执行的method
     * @param args 被代理类执行method所用参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("proxy class === " + proxy.getClass()); // 获取代理类clz  com.sun.proxy.$Proxy0

        for (Class<?> parentClz : proxy.getClass().getInterfaces()) {
            System.out.println("parent class is " + parentClz.getName());
        }

        // 获取代理类的interface

        String methodName = method.getName();

        // 根据method不同选择不同的代理行为，SpringAOP也是类似原理
        if (methodName == "eat"){
            System.out.println("handler process before eat " + args[0]);
            method.invoke(target, args);
            System.out.println("handler process after eat " + args);

        }else if (methodName == "run"){
            System.out.println("handler process before run ");
            method.invoke(target);
            System.out.println("handler process after run ");
        }
        return null;
    }
}
