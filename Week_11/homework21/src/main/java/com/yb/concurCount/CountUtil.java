package com.yb.concurCount;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * 分布式计数器
 *
 * @auther yb
 * @date 2021/1/22 20:38
 */
public class CountUtil {
    private static RedissonClient client = initClient();
    private static RedissonClient initClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6380"); // 必须连接master redis
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;

    }
    public static Long getCount(){
        RAtomicLong count1 = client.getAtomicLong("count1");
        return count1.incrementAndGet();
    }
}
