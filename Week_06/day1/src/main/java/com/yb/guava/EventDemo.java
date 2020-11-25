package com.yb.guava;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDemo {
    private String name;
    private String level;
}
