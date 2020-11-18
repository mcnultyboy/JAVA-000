package com.singleton;

/***
 * 懒汉模式配合双检锁
 *
 * 需要用到同步锁，维护不友好
 *
 * @auther yb
 * @date 2020/11/18 12:24
 */
public class Singleton2 {
    private static Singleton2 singleton;

    public static void main(String[] args) {
        Singleton3 singleton = Singleton3.getSingleton();
        System.out.println(singleton);
    }

    private Singleton2() {
    }

    public static Singleton2 getSingleton(){
        if (singleton == null){
            synchronized (Singleton2.class){
                if (singleton == null){
                    singleton = new Singleton2();
                }
            }
        }
        return singleton;
    }
}
