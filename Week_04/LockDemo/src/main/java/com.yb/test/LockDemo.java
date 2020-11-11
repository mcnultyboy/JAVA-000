package com.yb.test;

import java.util.concurrent.locks.ReentrantLock;

/***
 * 1.为什么需要lock？
 * synchronize存在的问题如下：
 *   1）获取锁时不能打断只能阻塞
 *   2）不能设置超时，导致耗时
 *   3）不能尝试性获取锁，并立马返回结果
 *   4）不支持条件变量，即不方便线程之间的同步
 * lock可以解决以上问题，所以才有了lock
 *
 * 2.lock以及condition功能
 *   lock解决互斥问题
 *   condition解决同步问题
 * 3.lock的原理
 *   利用了volatile的Happen-Before原理，内部维护了一个volatile修饰的state变量，
 *   第一次加解锁：在枷锁的时候state +=1,业务逻辑更改共享数据，释放的时候state -=1。
 *   第二次加解锁：枷锁时，读取state，由于第一次的共享数据在state之前，所以这一次共享数据最新。
 *
 * 4.可重入锁
 *   表示同一把锁，之前如果已经获取到锁，则再次自动获取，不会阻塞。
 *
 * @auther yb
 * @date 2020/11/10 19:59
 */
public class LockDemo {
    public static int sum;
    private ReentrantLock lock;
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock myLock = new ReentrantLock(true); // 公平锁，等待越久，越优先
        LockDemo test1 = new LockDemo(myLock);
        LockDemo test2 = new LockDemo(myLock);
        Thread t1 = new Thread(() -> {
            test1.doSum();
        });

        Thread t2 = new Thread(() -> {
            test2.doSum();
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(LockDemo.sum); // 2000
    }

    public LockDemo(ReentrantLock lock) {
        this.lock = lock;
    }

    public void doSum(){
        for (int i = 0; i < 1000; i++) {
            try {
                lock.lockInterruptibly(); // 加锁
                sum += 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock(); // finally中释放锁，最佳实践
            }
        }
    }
}
