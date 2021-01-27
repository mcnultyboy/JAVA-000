package com.yb.pubsub;

import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class RedisPub {
    public static void main(String[] args) throws InterruptedException {
        // 分别创建生产者和消费者的客户端，注意不能使用同一个客户端
        Jedis jedisPub = new Jedis("localhost", 6380);
        Jedis jedisSub = new Jedis("localhost", 6380);
        new Thread(()->{ // 另起线程监听消息 到 topic1 channel
            System.out.println("topic1 sub start");
            jedisSub.subscribe(new JedisSub(), "topic1");
            System.out.println("topic1 sub end");
        }).start();
        for (int i = 0; i < 10; i++) { // 发布消息 到topic1 channel
            TimeUnit.SECONDS.sleep(2);
            jedisPub.publish("topic1", "topic1 msg" + i);
        }
        jedisSub.close();
        jedisPub.close();
    }
}
