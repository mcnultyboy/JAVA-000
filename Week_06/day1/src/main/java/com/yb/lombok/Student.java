package com.yb.lombok;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Data
@Builder
@Log
public class Student {
    private String name;
    private int age;

}
