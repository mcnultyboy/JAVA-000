package com.yb.concurLock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisLockUtil {
    private static RedissonClient client = initClient();

    private static RedissonClient initClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6380"); // 必须连接master redis
        return Redisson.create(config);

    }

    public static RLock getLock(String lockName) {
        return client.getLock(lockName);
    }
}
