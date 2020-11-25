package com.yb.lombok;

import lombok.extern.java.Log;

@Log
public class LombokTest {

    public static void main(String[] args) {
        log.info("this is lombok log");
        Student zhangsan = Student.builder()
                .age(18)
                .name("zhangsan")
                .build();
        log.info(zhangsan.toString());
    }
}
