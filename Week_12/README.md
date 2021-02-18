# 作业

- [ ] 1、（必做）搭建ActiveMQ服务，基于JMS，写代码分别实现对于queue和topic的消息 

生产和消费，代码提交到github。 

- [ ] 2、（选做）基于数据库的订单表，模拟消息队列处理订单： 

  1）一个程序往表里写新订单，标记状态为未处理(status=0); 

  2）另一个程序每隔100ms定时从表里读取所有status=0的订单，打印一下订单数据， 

然后改成完成status=1； 

​	   3）（挑战☆）考虑失败重试策略，考虑多个消费程序如何协作。 

- [ ] 3、（选做）将上述订单处理场景，改成使用ActiveMQ发送消息处理模式。 

- [ ] 4、（选做）使用java代码，创建一个ActiveMQ Broker Server，并测试它。

# 作业1

JMS java message service，即java 消息服务，提供异步的消息支持。

## 安装并启动

1.[下载地址](http://activemq.apache.org/components/classic/download/)

2.下载后直接解压缩直接就能用（免安装）。

3.目录介绍

​	bin/是服务启动相关的命令文件所在目录

​	data/是默认持久化文件所在目录

​	docs/里面放的是用户手册

​	conf/是配置文件所在目录，任何配置文件修改后,必须重启ActiveMQ,才能生效.

4.常用的配置文件，由于是java写的，所以有很多spring相关的功能

​	1).activemq.xml 
就是spring配置文件。配置的是ActiveMQ应用使用的默认对象组件.
transportConnectors标签 - 配置链接端口信息的. 其中的端口号61616是ActiveMQ对外发布的 `tcp` 协议访问端口. 就是java代码访问ActiveMQ时使用的端口.

配置**安全认证和持久化**都是在这个文件里面。

​	2).jetty.xml
spring配置文件, ActiveMQ使用的是jetty提供HTTP服务,因此需要该文件用于配置jetty服务器的默认对象组件.

​	3).users.properties
内容信息: 用户名=密码

是用于配置客户端通过协议访问ActiveMQ时,使用的用户名和密码.

​	4).groups.properties

内容信息: 用户组=用户1,用户2（多个用户中间用逗号隔开）

类似于角色的概念，也类似于操作系统用户所在的用户组

5.启动时报错

报错信息，61616端口被占用，但是使用netstat -ano | findstr "61616" 并没有信息。最后重启解决问题。

## 消息可靠性



### 1.消息持久化：

配置在 `/config/activemq.xml` 的 `persistenceAdapter` 标签中，默认是存放到本地的log(kahadb/db.data)文件中，如下：

```xml
<persistenceAdapter>
            <kahaDB directory="${activemq.data}/kahadb" journalMaxFileLength="16mb"/>
</persistenceAdapter>
```

`kahadb`初始化的内容如下：

```properties
#Sat Feb 06 16:15:28 CST 2021
pageSize=4096
fileType=org.apache.activemq.store.kahadb.disk.page.PageFile
freePages=0
cleanShutdown=false
metaDataTxId=10
lastTxId=11
fileTypeVersion=1
```

还可以使用其他类型：

```
(1)、kahaDB文件持久化；
(2)、jdbc持久化；
(3)、levelDB文件持久化；
(4)、jdbc结合日志持久化；
```

ActiveMQ支持持久化，从而保证MQ宕机之后消息不会丢失。相当于有2分介质，一份内存，一份持久化。

默认策略，是由发送端设置的，如果没有设置其他参数，queue默认持久化，topic默认不持久化。持久化的参数会在消息的header中体现。

经过测试，MQ重启之后，queue中的消息还在，topic中的消息已经掉了。



对topic发送时设置持久化：

```java
String topicName = "topic1";
Topic topic1 = session.createTopic(topicName);
MessageProducer producer = session.createProducer(topic1);
```



### 2.订阅者的ack

持久化的数据删除的时机，当所有的订阅者接收到消息并ack之后，该条持久化消息才会被删除。若部分订阅者重启，其他订阅者会收到重复消息么？

mq将消息持久化到本地，并记录每个消费者的clientID信息，当消费者上线之后，将中间堆积的消息挨个发送。

当订阅者的ack模式设置为 Session.CLIENT_ACKNOWLEDGE，如果消费者收到消息，但是没有使用 message.acknowledge()就宕机时，再次恢复时，mq会进行消息重发，如下：

```java
		String brokerURL = "tcp://localhost:61616";
        //1.创建连接工厂,按照指定的url地址,采用默认帐号密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);

        //2.通过连接工厂,获得连接connection
        Connection connection = factory.createConnection();

        //用户yb订阅了
        connection.setClientID("Consumer_Queue_yb");
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE); // ack模式设置为 Session.CLIENT_ACKNOWLEDGE

        //4.创建目的地(具体是列队还是主题topic);
        Queue queue = session.createQueue("queue-yb");
        MessageConsumer consumer = session.createConsumer(queue);
        //启动订阅
        // 设置listener，用来监听消息
        consumer.setMessageListener((message) -> {
            if (message instanceof TextMessage) {
                try {
                    String text = ((TextMessage) message).getText();
                    System.out.println(text);
                    message.acknowledge(); // 如果没有ack，则当client重启时，mq会重发(没有次数限制)
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
```

消息重发和session设置有关

```
一 在不带事务的 Session 中，一条消息何时和如何被签收取决于Session的设置。 

1．Session.AUTO_ACKNOWLEDGE 

当客户端从 receive 或 onMessage成功返回时，Session 自动签收客户端的这条消息的收条。

2．Session.CLIENT_ACKNOWLEDGE 

客户端通过调用消息的 acknowledge 方法签收消息。注意，此时会ack这个session的所有消息

3.ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE

客户端通过调用消息的 acknowledge 方法签收消息。此时只会ack这一条消息。

二 在带事务的 Session 中，签收自动发生在事务提交时。如果事务回滚，所有已经接收的消息将会被再次传送。其实这里的Session.CLIENT_ACKNOWLEDGE 用处不大。

session = connection.createSession(Boolean.TRUE, Session.CLIENT_ACKNOWLEDGE);

```



消息重发的场景：

```
在发生以下情形时，消息会给重发给客户端：

使用了一个事务性的会话且调用了rollback()方法。
在调用commit()方法前一个事务性的会话被关闭了。
一个会话使用CLIENT_ACKNOWLEDGE的ACK模式，且调用了Session.recover()方法。
一个客户端连接超时（可能正被执行的代码执行的时间超过配置的超时时间）。
```





### 3.发布者的同步发送机制

productor.send(Massage)是没有返回值的，一般认为没有异常则表示消息已经发送到mq中。也可能因为网络抖动，导致消息没有发送到mq，可以开启同步发送策略。

开启同步发送时，消息到达broker，需要broker返回response给发送端，否则发送端抛出异常，来保证发送端的可靠性。

异步发送时，broker不会返回response，发送端也不会校验response。



### 4.同步和异步的开关

默认情况，非持久化消息都是异步的；持久化时，事务是异步的，非事务是同步的。这是因为异步的效率更高。

Consumer消费消息的风格有2种: 同步/异步..使用consumer.receive()就是同步，使用messageListener就是异步；在同一个consumer中，我们不能同时使用这2种风格，比如在使用listener的情况下，当调用receive()方法将会获得一个Exception。两种风格下，消息确认时机有所不同。

同步调用时，在消息从receive方法返回之前，就已经调用了ACK；因此如果Client端没有处理成功，此消息将丢失(可能重发，与ACK模式有关)。

但是，由于抛出异常后，一般当前线程都会终止，所以receive的处理必须交给线程池来处理，才会重发。

基于异步调用时，ack是在onMessage方法返回之后，如果onMessage方法异常，会导致消息不能被ACK，会触发重发。

不同的consumer之间互不影响。



## 发布订阅模式

topic模式，只有提前订阅了该topic的消费者才能收到消息，订阅之前产生的消息不能被收到。



## 参考资料

[1.activeMQ安装与使用](https://www.cnblogs.com/biehongli/p/11522793.html)

