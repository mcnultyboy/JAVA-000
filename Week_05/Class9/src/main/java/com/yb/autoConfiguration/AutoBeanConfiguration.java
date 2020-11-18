package com.yb.autoConfiguration;

import com.yb.configurationBean.BeanConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

// @Configuration 为了测试spring.factories，此处将注解关闭

public class AutoBeanConfiguration {

    @Bean
//    @ConditionalOnMissingBean(BeanConfig.class) // 类注解则表示对该类中所有的Bean生效
    @ConditionalOnMissingClass({"com.yb.autoConfiguration.MyCondition.class"})
    OrderByAutoConfig getOrderByAutoConfig(){
        return new OrderByAutoConfig();
    }

    @Bean
    @Conditional({MyCondition.class}) // 使用MyCondition.getMatchOutcome()完成自定义校验
    ProductByAutoConfig getProductByAutoConfig(){
        return new ProductByAutoConfig();
    }

}
