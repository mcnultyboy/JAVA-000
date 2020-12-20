package com.yb.MultiDataSource1.controller;

import com.yb.MultiDataSource1.entity.Order;
import com.yb.MultiDataSource1.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j
@RestController
public class OrderController {
    @Autowired
    private OrderMapper orderMapper;


    // 单条件则多次路由并union
    @GetMapping("/query/{orderId}")
    public Order queryByOrderId(@PathVariable("orderId") long orderId){
        return orderMapper.selectByOrderId(orderId);
    }

    // 通过双条件，唯一路由
    @GetMapping("/query/{userId}/{orderId}")
    public Order queryByUserIdAndOrderId(@PathVariable("userId") long userId, @PathVariable("orderId") long orderId){
        log.info("======================");
        return orderMapper.selectByUserIdAndOrderId(userId, orderId);
    }

    @PostMapping("/createOrder")
    public Order createOrder(){
        Random random = new Random();
        long userId = random.nextInt(99999999);
        long orderId = random.nextInt(99999999);
        Order order = new Order(orderId, userId, new Double(1), System.currentTimeMillis(), System.currentTimeMillis());
        orderMapper.save(order);
        return order;
    }
}
