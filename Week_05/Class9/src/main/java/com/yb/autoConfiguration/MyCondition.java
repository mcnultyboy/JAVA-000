package com.yb.autoConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

/***
 * SpringBootCondition 为条件判断类，需要重写 getMatchOutcome 完成条件判断，只有条件满足时才加载bean
 *
 * @auther yb
 * @date 2020/11/14 15:32
 */
public class MyCondition implements Condition {



//    @Override
//    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
//        Environment env = context.getEnvironment();
//        String flag = env.getProperty("loadProduct");
//        System.out.println("flag == " + flag);
//        if (flag == "ON"){
//            return  new ConditionOutcome(true, "match");
//        }
//        return new ConditionOutcome(false, "not match");
//    }
    /**根据application.properties中配置得loadProduct标识来判断是否加载bean*/
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment env = conditionContext.getEnvironment();
        String flag = env.getProperty("loadProduct");
        System.out.println("flag == " + flag);
        if ("ON".equals(flag)){
            System.out.println("match ok");
            return true;
        }
        return false;
    }
}
