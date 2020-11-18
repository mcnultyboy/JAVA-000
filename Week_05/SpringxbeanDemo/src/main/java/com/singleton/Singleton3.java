package com.singleton;

/***
 * 静态内部类
 *
 * 需要使用内部类，维护不友好，而且增加了阅读的困扰
 *
 * @auther yb
 * @date 2020/11/18 12:27
 */
public class Singleton3 {
    private Singleton3() {
    }

    public static Singleton3 getSingleton(){
        return Inner.singleton;
    }

    static class Inner{
        private static Singleton3 singleton = new Singleton3();
    }
}
