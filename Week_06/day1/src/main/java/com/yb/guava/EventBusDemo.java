package com.yb.guava;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventBusDemo {
    // EV一般是全局共享的
    static EventBus bus = new EventBus();
    static {
        // 将EventBusDemo注册到EB上，即完成订阅
        bus.register(new EventBusDemo());
    }

    public static void main(String[] args) {
        // 创建事件
        EventDemo event = EventDemo.builder()
                .name("回家吃饭")
                .level("1")
                .build();
        // 发布事件
        bus.post(event);
    }

    // Subscribe注解用于接收发布的消息
    @Subscribe
    public void handleEvent(EventDemo event){
        log.info("receive event from bus, event = {}", event);
    }
}
