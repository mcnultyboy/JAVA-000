package com.yb.MultiDataSource1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
    private long order_id;
    private long user_id;
    private Double price;
    private long create_time;
    private long update_time;
}
