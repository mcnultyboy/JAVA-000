package com.yb.controller;

import com.alibaba.fastjson.JSON;
import com.yb.entity.Student;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RedisController {

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @PostMapping("/put/{name}")
    public String redisTemplatePut(@PathVariable("name") String name){
        Student student = new Student(name, System.currentTimeMillis());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(name, student);
        return JSON.toJSONString(student);
    }

    @GetMapping("/get/{name}")
    public String redisTemplateGet(@PathVariable("name") String name) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Student student = (Student)valueOperations.get(name);
        return JSON.toJSONString(student);
    }
}
