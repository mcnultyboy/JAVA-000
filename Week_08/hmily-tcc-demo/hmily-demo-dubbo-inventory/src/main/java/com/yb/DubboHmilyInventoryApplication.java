package com.yb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:applicationContext.xml"})
public class DubboHmilyInventoryApplication {
    /**
     * main.
     *
     * @param args args
     */
    public static void main(final String[] args) {
        SpringApplication springApplication = new SpringApplication(DubboHmilyInventoryApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
