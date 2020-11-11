package com.yb.test;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/***
 * conditon解决线程同步问题
 *
 * 使用condition实现阻塞队列
 *
 * @auther yb
 * @date 2020/11/10 20:29
 */
public class ConditionDemo {
    public  ArrayList<Integer> queue = new ArrayList<>(10);
    private ReentrantLock lock;
    private Condition notFull = lock.newCondition();
    private Condition notEmput = lock.newCondition();

    public ConditionDemo(ReentrantLock lock) {
        this.lock = lock;
    }

    // 入队
    void enq(int item) throws InterruptedException {
        lock.lock();
        try {
            // 满了则等待
            while (queue.size() == 10) {
                notFull.await(); // 等待直到不是满了
            }
            queue.add(item);
            notEmput.notifyAll();
        } finally {
            lock.unlock();
        }

    }

    // 出队
    Integer deq() throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == 0) {
                notEmput.await();
            }
            Integer item = queue.remove(queue.size() - 1);
            notFull.notifyAll();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
