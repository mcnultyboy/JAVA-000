package com.singleton;

/***
 * 恶汉模式,效率最高，且不容易出错
 *
 * @auther yb
 * @date 2020/11/18 12:21
 */
public class Singleton1 {
    private final  static Singleton1 singleton = new Singleton1();

    private Singleton1() {
    }

    public static Singleton1 getSingleton(){
        return singleton;
    }
}
