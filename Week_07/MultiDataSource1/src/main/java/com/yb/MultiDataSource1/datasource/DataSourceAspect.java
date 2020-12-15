package com.yb.MultiDataSource1.datasource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/***
 * DataSourceSwith aop 类，对注解标注的method进行增强
 * 需要引入spring-boot-starter-aop依赖
 *
 * @auther yb
 * @date 2020/12/3 20:28
 */
@Slf4j
@Aspect // 定义切面
@Component
public class DataSourceAspect {
    private static AtomicInteger visitNum = new AtomicInteger(0);
    private static final int SLAVE_SIZE = 2;

    // 定义切点为注解
    @Pointcut("@annotation(com.yb.MultiDataSource1.datasource.DataSourceSwith)")
    public void dsPointCut(){}

    // 定义增强
    @Around("dsPointCut()")
    public Object switchDataSource(ProceedingJoinPoint point){
        MethodSignature signature = (MethodSignature)point.getSignature();
        DataSourceType dataSourceType = signature.getMethod().getAnnotation(DataSourceSwith.class).value();
        // 如果为SLAVE则表示需要进行的负载均衡到各个slave
        if (dataSourceType.equals(DataSourceType.SLAVE)){
            int slaveRouteNum = (visitNum.incrementAndGet() % SLAVE_SIZE) + 1 ; // SLAVE从1开始配置
            String slaveName = dataSourceType.name() + slaveRouteNum;
            log.info("route, slaveName = {}", slaveName);
            DynamicDataSourceContextHolder.setDataSourceType(slaveName);
        } else {
            log.info("no route");
            // 设置当前线程数据源类型
            DynamicDataSourceContextHolder.setDataSourceType(dataSourceType.name());
        }

        try{
            return point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            // 使用完毕之后清除数据源缓存
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
        return null;
    }
}
