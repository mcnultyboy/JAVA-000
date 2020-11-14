# Week_05 作业
## 1、（选做）使Java里的动态代理，实现一个简单的AOP。
### 1.作业位置 Project Class9 Package com.yb.jdkProxy
### 2.作业步骤
1. 创建handler类`StuInvocationHandler`实现`InvocationHandler`，重写`invoke`方法()，handler类构造函数入参为被代理类<br>
2. 创建被代理类`Student`及其父接口`Person`，使用父接口的原因为JDK的动态代理的被代理类必须有父接口。如果是CGLIB，则不必须。<br>
3. 创建测试类`ProxyTest`，进行测试，测试步骤如下：<br>
* 创建被代理类实例<br>
* 创建Handler，并在构造函数中注入被代理类实例<br>
* 使用Proxy工具类创建代理类实例，Proxy.newProxyInstance(classLoader, 被代理类父接口class，handler实例)<br>
* 使用代理类执行行为<br>
### 3.作业疑问
1. 还有没有其他方式实现JDK动态代理？<br>
2. 本实现方式有没有需要改进的缺点？<br>
3. 在执行的时候会打印重复的信息，为什么？<br>
* 答：这个是 idea 的配置导致的，当你 debug 的时候，idea 会调用 ToString 方法来显示对象信息<br>
## 2、（必做）写代码实现Spring Bean的装配，方式越多越好（XML、Annotation都可以）,提交到Github。
### 1.作业位置 Project Class9
### 2.作业步骤
1. Class9 project 引入`SpringBoot`，并创建启动类`SpringBeanDemoApplication`<br>
2. 使用注解方式装配bean<br>
* 创建`OrderByAnno`entity，并使用@Component注解<br>
* 启动`SpringBeanDemoApplication`，会自动加载启动类所在目录以及子目录中所有被注解的bean<br>
* 装配成功<br>
3. 使用XML的方式装配bean<br>
* 创建spring.xml作为装配bean的xml<br>
* 创建`ProductByXml`与`OrderByXml`entity，并在spring.xml中配置`ProductByXml` `OrderByXml`的bean定义<br>
* `myProduct`作为Product的自定义BeanId，同时将`orderByXml`注入到`myProduct`中<br>
* 创建`XmlConfigTest`测试类，并使用`ClassPathXmlApplicationContext`加载spring.xml完成bean的装配<br>
4. 注解与xml混合方式装配bean<br>
* 结合2 3 两种方式，在`ClassPathXmlApplicationContext`启动类中使用`@ImportResource("classpath:spring.xml")`加载配置文件<br>
* 通过`ApplicationContextAware`在启动类bean初始化之后注入Spring上下文对象，注意不是在所有bean都初始化完毕之后注入<br>
* 通过`ApplicationRunner`，在SpringBoot启动之后，即所有的bean都初始化之后，验证bean的注入情况<br>
5. 注解使用javaconfig的方式注解<br>
* `@configuration`：标明是一个配置类，用来申明beans。其作用相当与xml配置中的beans.<br>
* `@configuration`是Spring的注解，非SpringBoot，若只使用Spring需要配合包扫描才生效，若Spring-boot项目则保证启动类spring boot main的主入口，在所有配置类的最上层就行。<br>
* `@Bean`表示函数的返回值作为bean交给IOC管理<br>
* 创建@configuration标注的`BeanConfig`类，`getOrderByConfig`方法用`@Bean`标注，将`OrderByConfig`纳入IOC管理 <br>
6. SpringBoot 自动配置配置bean升级版(加入condition等)<br>
* 1.实现原理：<br>
* 基于在classpath中出现的类、application.properties，环境上下文的一些配置，根据这些资源作为配置依据，进行自动配置得过程。<br>
* @EnableAutoConfiguration触发AutoConfigurationImportSelector.getAutoConfigurationEntry()去加载所有mudules以及依赖模块中META-INF/spring.factories文件中配置class<br>
* 2.步骤:<br>
* 在本项目中创建META-INF/spring.factories，并配置`org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.yb.autoConfiguration.AutoBeanConfiguration`来标识配置类<br>
* `AutoBeanConfiguration`中配置了`OrderByAutoConfig`&`ProductByAutoConfig`，其中`ProductByAutoConfig`使用`@Conditional({MyCondition.class})`来校验。`MyCondition` 中根据application.propertis中配置的开关来配置bean。<br>
7. 问题<br>
@ConditionalOnMissingClass 不生效，无论MyCondition是否存在，都会创建OrderByAutoConfig 这个bean，配置错误？<br>
想要达到如果项目中没有HaHa.class就初始化并，现在的项目中就没有HaHa.class却编译报错找不到HaHa.class，陷入死循环
```java
    @ConditionalOnMissingClass({"com.yb.autoConfiguration.MyCondition.class"}) // 不生效
    OrderByAutoConfig getOrderByAutoConfig(){
        return new OrderByAutoConfig();
    }
    @ConditionalOnMissingClass({HaHa.class}) // 编译报错找不到HaHa.class
    OrderByAutoConfig getOrderByAutoConfig(){
        return new OrderByAutoConfig();
    }
```
