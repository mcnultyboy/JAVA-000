package com.yb.lockTest;

import com.yb.concurLock.RedisLockUtil;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;


public class LockDemo1 {

    public static void main(String[] args) throws InterruptedException {
        RLock lock = RedisLockUtil.getLock("lock1");
        try{
            boolean isLocked = lock.tryLock();
            System.out.println(isLocked);
            System.out.println("lock1 is locked in demo1");
            for (int i = 0; i < 20; i++) {
                System.out.println("demo1 do " + i);
                TimeUnit.SECONDS.sleep(1);
            }
        } finally {
            lock.unlock();
            System.out.println("demo1 unlock" + System.currentTimeMillis());
        }
    }
}
