package com.yb.pubsub;

import redis.clients.jedis.JedisPubSub;

public class JedisSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        // diy do something...
        System.out.println("onMessage  Channel:" + channel + ",Message:" + message);
    }

}
