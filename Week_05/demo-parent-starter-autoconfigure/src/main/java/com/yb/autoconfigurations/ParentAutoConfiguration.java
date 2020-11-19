package com.yb.autoconfigurations;

import com.yb.parent.Parent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

public class ParentAutoConfiguration {

    // 只有在配置文件中开启了才会注入该bean
    @Bean
    @ConditionalOnProperty(prefix = "parent",name = "enable", havingValue = "true", matchIfMissing = false)
    public Parent parent(){
        return  new Parent("parent in starter");
    }
}
