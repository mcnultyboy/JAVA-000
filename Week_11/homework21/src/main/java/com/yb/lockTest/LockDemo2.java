package com.yb.lockTest;

import com.yb.concurLock.RedisLockUtil;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;


public class LockDemo2 {

    public static void main(String[] args) throws InterruptedException {
        RLock lock = RedisLockUtil.getLock("lock1");
        try{
            lock.lock();
            System.out.println("lock1 is locked in demo2 " + System.currentTimeMillis());
            for (int i = 0; i < 10; i++) {
                System.out.println("demo2 do " + i);
                TimeUnit.SECONDS.sleep(1);
            }
        } finally {
            lock.unlock();
        }
    }
}
