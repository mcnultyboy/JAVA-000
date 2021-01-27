package com.yb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // 反序列化需要无参构造函数进行实例化
public class Student {
    private String name;
    private Long time;

}
