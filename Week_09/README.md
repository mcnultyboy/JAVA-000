学习笔记
# day1 作业三
改造自定义RPC的程序，提交到github： 
1）尝试将服务端写死查找接口实现类变成泛型和反射

# day2 作业二
## 1.rpc调用接口比如实现序列化接口
io.netty.handler.codec.EncoderException: java.lang.IllegalStateException: Serialized class io.kimmking.dubbo.demo.api.Account must implement java.io.Serializable
解决办法：implements Serializable

##2.客户端配置文件

server:
  port: 8188 # 需要配置端口，否则不能访问
spring:
  application:
    name: dubbo-demo-consumer
  main:
    allow-bean-definition-overriding: true
    #web-application-type: none 需要注释，否则不能启动tomcat

##3.dubbo admin控制台
	3.1下载地址：https://github.com/apache/dubbo-admin
	3.2需要修改配置文件为对应的zookeeper的地址，注意端口.文件路径：dubbo-admin-server/src/main/resources/application.properties
	3.3 mvn clean package 最好跳过测试
	3.4 访问控制台时，里面只有provider的接口信息，不会有consumer的信息。
	3.5 执行 java -jar E:\dubbo-admin\dubbo-admin-distribution\target\dubbo-admin-0.2.0-SNAPSHOT.jar,再访问8080端口即可。

##4.配置hmily
	4.1 由于自动配置中包含了mongo，所以需要排除依赖，否则加载mongo出错
	@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
	4.2 注意由于依赖了mysql，所以需要启动mysql，否则hmily启动报错
	E:\mysql\mysql-5.7.31-winx64-1-master-3306\bin
	执行 ./mysqld

##5.zookeeper 启动占用8080端口导致dubbo admin 不能启动问题
	5.1d zookeeper的8080端口没多大用，可以去掉
	解决办法：修改 zkServer.cmd 添加 -Dzookeeper.admin.enableServer=false。如下
	call %JAVA%  "-Dzookeeper.admin.enableServer=false" "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" "-Dzookeeper.log.file=%ZOO_LOG_FILE%" "-XX:+HeapDumpOnOutOfMemoryError" "-XX:OnOutOfMemoryError=cmd /c taskkill /pid %%%%p /t /f" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%" %*

##6.如果想要达到cancel的效果，必须在provider的try方法中增加异常机制。
	一旦try失败，则try中直接抛出异常，相当于冻结失败。则其他已经成功的try，则会执行对应的cancel函数。
	在consumer中进行异常抛出无用，只会回滚本地事务，不会执行provider的cancel，因为此时provider的已经comfirm。

##7.mybatis的mapper没有生成bean，导致service中注入bean失败。
	在启动类添加@MapperScan，再进行一次扫描即可。可能是版本不兼容的问题。其他没有用hmily的项目则只用了@Mapper注解即可。

##8.Mybatis进行update 操作不会进行属性回填
	解决办法：手动set，或者执行update之后执行再进行查询，属于同一事务，属于最新数据。

##9.幂等性问题
	try comfirm cancel使用version进行幂等校验。
	try 先进行version校验，通过了之后进行update 冻结，并version+1。同时account对应的version+1。
	如果try成功，用该业务account(已更新version)，再进行update 金额扣减。
	如果try失败，则其他业务account(已更新version)，再进行update 解冻。
##10.建表语句以及数据初始化
```sql
CREATE TABLE `account` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `dollarAmt` smallint(4) DEFAULT NULL,
  `rmbAmt` smallint(4) DEFAULT NULL,
  `dollarFrz` smallint(4) DEFAULT NULL,
  `rmbFrz` smallint(6) DEFAULT NULL,
  `version` smallint(4) DEFAULT NULL,
  `updateTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `account` VALUES ('1', 'zhangsan', 5, 14, 0, 0, 1, '2020-12-30 20:09:32');
INSERT INTO `account` VALUES ('2', 'lisi', 5, 14, 0, 0, 1, '2020-12-30 20:09:33');
```
