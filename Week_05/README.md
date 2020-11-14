# Week_05 作业
## 1、（选做）使Java里的动态代理，实现一个简单的AOP。
### 1.作业位置 Class9 project com.yb.jdkProxy
### 2.作业步骤
* 创建handler类`StuInvocationHandler`实现`InvocationHandler`，重写`invoke`方法()，handler类构造函数入参为被代理类<br>
* 创建被代理类`Student`及其父接口`Person`，使用父接口的原因为JDK的动态代理的被代理类必须有父接口。如果是CGLIB，则不必须。<br>
* 创建测试类`ProxyTest`，进行测试，测试步骤如下：<br>
>创建被代理类实例<br>
>创建Handler，并在构造函数中注入被代理类实例<br>
>使用Proxy工具类创建代理类实例，Proxy.newProxyInstance(classLoader, 被代理类父接口class，handler实例)<br>
>使用代理类执行行为<br>
### 3.作业疑问
1.还有没有其他方式实现JDK动态代理？<br>
2.本实现方式有没有需要改进的缺点？<br>
3.在执行的时候会打印重复的信息，为什么？<br>
<br>
## 2、（必做）写代码实现Spring Bean的装配，方式越多越好（XML、Annotation都可以）,提交到Github。
### 1.作业位置 Class9 project
### 2.作业步骤
1.
