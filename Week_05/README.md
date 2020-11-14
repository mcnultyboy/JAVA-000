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
* 通过`ApplicationRunner`，在SpringBoot启动之后，即所有的bean都初始化之后，验证bean的注入情况
