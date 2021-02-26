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

mq将消息持久化到本地，并记录每个消费者的clientID信息，当消费者上线之后，将中间堆积的消息挨个发送，已经收到消息的client不会被重复发送。

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
                    message.acknowledge(); // 如果没有ack，或者此处抛出异常，则会broker会重发，默认的重发次数是6次。重发次数用完依然失败，则broker将消息放到DLQ即死信队列
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
```

**消息重发和client的session设置有关，与发送者的session设置无关**

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

1.使用了非Session.AUTO_ACKNOWLEDGE 的应答方式，client没有显示ack消息，进行重启之后会重发
2.所有的应答方式中，一旦在 onMessage 函数中抛出异常，则消息都会重发6次(默认次数)。
```



消息重发策略设置，不是在active的config中，而是在程序中通过编码或者springBean的方式配置。

```java
		String brokerURL = "tcp://localhost:61616";
        //1.创建连接工厂,按照指定的url地址,采用默认帐号密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);

        // 设置消息重发策略
        RedeliveryPolicy policy = new RedeliveryPolicy();
        // 初始重发延迟时间 ms，默认1000
        policy.setInitialRedeliveryDelay(1000L);
        // 最大重传次数，达到最大重连次数后抛出异常。为-1时不限制次数，为0时表示不进行重传。
        policy.setMaximumRedeliveries(4);
        // 启用指数倍数递增的方式增加延迟时间。
        policy.setUseExponentialBackOff(true);
        // 重连时间间隔递增倍数，只有值大于1和启用useExponentialBackOff=true参数时才生效。倍数=2表示，重发时间间隔为1s 2s 4s 8s
        policy.setBackOffMultiplier(2);
        factory.setRedeliveryPolicy(policy);

        //2.通过连接工厂,获得连接connection
        Connection connection = factory.createConnection();
		......
```



### 2.1死信队列

**a.死信队列中数据的产生**

当一个消息被redelivered超过maximumRedeliveries(缺省为6次，具体设置请参考后面的链接)次数时，会给broker发送一个"Poison ack"，这个消息被认为是a poison pill，这时broker会将这个消息发送到DLQ，以便后续处理。

缺省的死信队列是ActiveMQ.DLQ，如果没有特别指定，死信都会被发送到这个队列。

注意，默认情况下topic的死信也是放到queue中，而不是topic中。但是没有测试成功。

**b.不使用缺省的死信队列**


缺省所有队列的死信消息都被发送到同一个缺省死信队列，不便于管理。可以通过individualDeadLetterStrategy或sharedDeadLetterStrategy策略来进行修改

```xml
<destinationPolicy>
            <policyMap>
              <policyEntries>
              	<!-- 设置所有队列，使用 '>' ，否则用队列名称 -->
                <policyEntry topic=">" >
                    <!-- The constantPendingMessageLimitStrategy is used to prevent
                         slow topic consumers to block producers and affect other consumers
                         by limiting the number of messages that are retained
                         For more information, see:

                         http://activemq.apache.org/slow-consumer-handling.html

                    -->
                  <pendingMessageLimitStrategy>
                    <constantPendingMessageLimitStrategy limit="1000"/>
                  </pendingMessageLimitStrategy>
                </policyEntry>

                <!-- 设置所有队列，使用 '>' ，否则用队列名称，此处表示对queue-yb这一个队列单独设置 -->
		        <policyEntry queue="queue-yb">
		          <deadLetterStrategy>
		            <!--
		                    queuePrefix:设置死信队列前缀
		                    useQueueForQueueMessages: 设置使用队列保存队列的死信；还可以设置useQueueForTopicMessages，使用queue来保存topic的死信
		            -->
		            <individualDeadLetterStrategy   queuePrefix="DLQ." useQueueForQueueMessages="true" />
		          </deadLetterStrategy>
		        </policyEntry>

              </policyEntries>
            </policyMap>
        </destinationPolicy>
```

经过测试，当超过重试次数时，会将消息放到[DLQ.queue-yb]这个队列中，其它队列以此类推。

注意，一旦消息被放到死信队列之后，原来的client就不会接收到该条消息。

**c.死信队列中消息的处理**

一般2种机制，

1 如果是异常的消息除了放到死信队列之外，业务应该作额外的处理，比如存在db中，通过跑批的方式处理db中的消息。

2 死信队列也是队列，同样可以消费。即通过其他的client消费死信队列中的消息。

**注意：经过测试，ActiveMQ.DLQ 是对自定义死信队列的兜底，即自定义的死信队列中的消息形成的毒丸最终放到ActiveMQ.DLQ中。如果ActiveMQ.DLQ也消费失败，则该条消息被丢弃。**

### 2.2 消息过期

queue和topic在发送消息的时候都可以设置消息过期时间，单位ms，在有效期内可以消费，超过有效期该消息会被转移到死信队列。

queue的如下：

```java
		// 1.创建连接工厂，并使用指定的ip和activeMQ的tcp port
        String brokerURL = "tcp://localhost:61616";
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);
        // 2.使用工厂创建连接
        Connection connection = factory.createConnection();
        // 3.开启mq连接
        connection.start();
        // 4.创建session,
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);// 不使用事务, 接收方自动ack
        // 5.创建destination
        String queueName = "queue-yb";
        Queue queue1 = session.createQueue(queueName);
        MessageProducer producer = session.createProducer(queue1);
        // 创建message并发送
        TextMessage textMessage = session.createTextMessage("queue-yb," + System.currentTimeMillis());
        // 设置过期时间2分钟，单位ms
        producer.send(textMessage, DeliveryMode.PERSISTENT, 4, 1000*60*2);
        // 9、关闭资源。
        producer.close();// 关闭producer
        session.close();// 关闭session
        connection.close();// 关闭connection
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

topic模式，只有提前订阅了该topic的消费者才能收到消息，订阅之前产生的消息不能被收到。订阅之后如果进行重启，会收到期间产生的消息。



## 参考资料

[1.activeMQ安装与使用](https://www.cnblogs.com/biehongli/p/11522793.html)

# 提升2 ActiveMQ的集群与高可用

## client端的高可用

### **现在的问题**

1.同一应用内consumer端负载均衡的问题：同一个应用上的一个持久订阅不能使用多个consumer来共同承担消息处理功能。因为每个都会获取所有消息。queue模式可以解决这个问题，broker端又不能将消息广播发送到多个应用端。所以，既要发布订阅，又要让消费者分组，这个功能jms规范本身是没有的。

2.同一应用内consumer端failover的问题：由于只能使用单个的持久订阅者，如果这个订阅者出错，则应用就无法处理消息了，系统的健壮性不高。相当于只是单线程处理，一旦这个线程挂掉，就不能进行消费。

### **解决问题的办法**

ActiveMQ中实现了虚拟Topic的功能。使用起来非常简单。

对于消息发布者来说，就是一个正常的Topic(必须使用topic)，名称以VirtualTopic.开头(约定)。例如VirtualTopic.TEST。

对于消息接收端来说，所有的应用都使用Queue来接收(必须使用Queue)，并且使用Consumer.xxx.xxx.VirtualTopic.xxx的开头格式，其中Consumer.与VirtualTopic.必须有，Consumer前缀为默认值，可以修改。例如Consumer.A.VirtualTopic.TEST，说明它是名称为A的接收端，同理Consumer.B.VirtualTopic.TEST说明是一个名称为B的接收端。

同时A或者B这个应用中，可以使用**多个queue**来接收，同样queue名称的消费者**会平**分所有消息。

### **实现**

A应用内，使用两个Queue来均分收到的全量消息；B应用内，使用1个Queue来接收全量消息。

A应用消费

```java
public class JmsConsumer_Topic_Virtual_A1 {
    public static void main(String[] args) throws JMSException {
        String brokerURL = "tcp://localhost:61616";
        //1.创建连接工厂,按照指定的url地址,采用默认帐号密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);

        //2.通过连接工厂,获得连接connection
        Connection connection = factory.createConnection();

        //用户wangwu订阅了
        connection.setClientID("Consumer1_topic_yb_A1");
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4.创建目的地(具体是列队还是主题topic);
        MessageConsumer consumer = session.createConsumer(new ActiveMQQueue("Consumer.A.VirtualTopic.TEST"));
        //启动订阅

        consumer.setMessageListener((message) -> {
            if (message instanceof TextMessage) {
                try {
                    String text = ((TextMessage) message).getText();
                    System.out.println("Topic-A1 receive " + text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
```

```java
public class JmsConsumer_Topic_Virtual_A2 {
    public static void main(String[] args) throws JMSException {
        String brokerURL = "tcp://localhost:61616";
        //1.创建连接工厂,按照指定的url地址,采用默认帐号密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);

        //2.通过连接工厂,获得连接connection
        Connection connection = factory.createConnection();

        //用户wangwu订阅了
        connection.setClientID("Consumer1_topic_yb_A2");
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4.创建目的地(具体是列队还是主题topic);
        MessageConsumer consumer = session.createConsumer(new ActiveMQQueue("Consumer.A.VirtualTopic.TEST"));
        //启动订阅

        consumer.setMessageListener((message) -> {
            if (message instanceof TextMessage) {
                try {
                    String text = ((TextMessage) message).getText();
                    System.out.println("Topic-A2 receive " + text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
```

B应用消费

```
public class JmsConsumer_Topic_Virtual_B {
    public static void main(String[] args) throws JMSException {
        String brokerURL = "tcp://localhost:61616";
        //1.创建连接工厂,按照指定的url地址,采用默认帐号密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);

        //2.通过连接工厂,获得连接connection
        Connection connection = factory.createConnection();

        //用户wangwu订阅了
//        connection.setClientID("Consumer1_topic_yb_B");
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4.创建目的地(具体是列队还是主题topic);
        MessageConsumer consumer = session.createConsumer(new ActiveMQQueue("Consumer.B.VirtualTopic.TEST"));
        //启动订阅

        consumer.setMessageListener((message) -> {
            if (message instanceof TextMessage) {
                try {
                    String text = ((TextMessage) message).getText();
                    System.out.println("Topic-B receive " + text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
```



生产

```java
public class JmsProduce_Topic_Virtual {
    public static void main(String[] args) throws JMSException {
        // 1.创建连接工厂，并使用指定的ip和activeMQ的tcp port
        String brokerURL = "tcp://localhost:61616";
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);
        // 2.使用工厂创建连接
        Connection connection = factory.createConnection();
        // 3.开启mq连接
        connection.start();
        // 4.创建session,
        Session session = connection.createSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);// 不使用事务, 接收方自动ack
        // 5.创建destination,topic(queue)
        // 6.创建creater
        String topicName = "VirtualTopic.TEST";
        Topic topic = session.createTopic(topicName);
        MessageProducer producer = session.createProducer(topic);
        // 创建message并发送
        for (int i = 0; i < 10; i++) {
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(topicName + "====" +i);
            producer.send(textMessage);
            System.out.println("send" + i);
        }
        // 9、关闭资源。
        producer.close();// 关闭producer
        session.close();// 关闭session
        connection.close();// 关闭connection
    }
}
```

### 测试

发送消息

```
send0
send1
send2
send3
send4
send5
send6
send7
send8
send9
```

A应用中A1接收

```
Topic-A1 receive VirtualTopic.TEST====0
Topic-A1 receive VirtualTopic.TEST====2
Topic-A1 receive VirtualTopic.TEST====4
Topic-A1 receive VirtualTopic.TEST====6
Topic-A1 receive VirtualTopic.TEST====8
```

A应用中A2接收

```
Topic-A2 receive VirtualTopic.TEST====1
Topic-A2 receive VirtualTopic.TEST====3
Topic-A2 receive VirtualTopic.TEST====5
Topic-A2 receive VirtualTopic.TEST====7
Topic-A2 receive VirtualTopic.TEST====9
```

B应用接收

```
Topic-B receive VirtualTopic.TEST====0
Topic-B receive VirtualTopic.TEST====1
Topic-B receive VirtualTopic.TEST====2
Topic-B receive VirtualTopic.TEST====3
Topic-B receive VirtualTopic.TEST====4
Topic-B receive VirtualTopic.TEST====5
Topic-B receive VirtualTopic.TEST====6
Topic-B receive VirtualTopic.TEST====7
Topic-B receive VirtualTopic.TEST====8
Topic-B receive VirtualTopic.TEST====9
```



### 修改消费端的queue名称前缀

```xml
<beans  
  xmlns="http://www.springframework.org/schema/beans"  
  xmlns:amq="http://activemq.apache.org/schema/core"  
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd  
  http://activemq.apache.org/schema/core http://activemq.apache.org/schema/core/activemq-core.xsd">  
   
<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer" />  
   
  <broker xmlns="http://activemq.apache.org/schema/core">  
    <destinationInterceptors>  
      <virtualDestinationInterceptor>  
        <virtualDestinations>  
          <virtualTopic name=">"prefix="VirtualTopicConsumers.*."selectorAware="false"/>  
        </virtualDestinations>  
      </virtualDestinationInterceptor>  
    </destinationInterceptors>  
   
  </broker>  
</beans>  


selectorAware属性则表明如果consumer端有selector，则只有匹配selector的消息才会分派到对应的queue中去。
```



