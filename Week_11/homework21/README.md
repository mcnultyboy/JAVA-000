# 作业要求

- [x] 2、（选做）分别基于jedis，RedisTemplate，Lettuce，Redission实现redis基本操作 

的demo，可以使用spring-boot集成上述工具。 

- [x] 4、（必做）基于Redis封装分布式数据操作： 
  - [x] 1）在Java中实现一个简单的分布式锁； 

  - [x] 2）在Java中实现一个分布式计数器，模拟减库存。 
- [x] 5、基于Redis的PubSub实现订单异步处理

# 实现步骤

## 作业2

spring-boot集成RedisTemplate

`pom.xml`

```xml
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.70</version>
        </dependency>

        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>3.14.1</version>
        </dependency>

        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```

`application.properties`

```properties
# Redis数据库索引（默认为0）
spring.redis.database=0  
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6380
# Redis服务器连接密码（默认为空） 如果没有配置密码，则不能设置此项，否则报连接错误
#spring.redis.password=
```

`配置RedisTemplate` **使用json序列化**

```java
@Configuration
public class RedisConfig {

    @Bean("redisTemplate") // StringRedisTemplate表示key value 都是String类型
    public RedisTemplate redisTemplate(RedisConnectionFactory factory){
        RedisTemplate template = new RedisTemplate();

        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);

        // 设置json序列化范围
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，PropertyAccessor.ALL表示field的get和set函数; 以及修饰符范围，JsonAutoDetect.Visibility.ANY 是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会抛出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        // 配置连接工厂
        template.setConnectionFactory(factory);
        // value采用json序列化和反序列化
        template.setValueSerializer(jacksonSeial);
        // key采用StringRedisSerializer来序列化和反序列化
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
    }
}
```

`entity` **注意加上无参构造函数**

```java
@Data
@AllArgsConstructor
@NoArgsConstructor // 反序列化需要无参构造函数进行实例化
public class Student {
    private String name;
    private Long time;
}
```

`controller` **需要使用ValueOperations**

```java

@RestController
public class RedisController {

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @PostMapping("/put/{name}")
    public String redisTemplatePut(@PathVariable("name") String name){
        Student student = new Student(name, System.currentTimeMillis());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(name, student);
        return JSON.toJSONString(student);
    }

    @GetMapping("/get/{name}")
    public String redisTemplateGet(@PathVariable("name") String name) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Student student = (Student)valueOperations.get(name);
        return JSON.toJSONString(student);
    }
}
```

`测试`

```
request localhost:8080/put/zhangsan
return  {"name":"zhangsan","time":1611733874699}

request localhost:8080/get/zhangsan
{"name":"zhangsan","time":1611733874699}
```



## 作业4

**lock.lock的本质是，lockName作为key，uuid+threadID作为value，加上过期时间，使用lua脚本执行，保证原子性。若key不存在则set到redis，存在则获取。由于涉及到set操作，所以必须连接主库，否则操作slave报readonly异常**。

`RedisLockUtil` redis分布式锁工具类

```java
public class RedisLockUtil {
    private static RedissonClient client = initClient();

    private static RedissonClient initClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6380"); // 必须连接master redis
        return Redisson.create(config);

    }

    public static RLock getLock(String lockName) {
        return client.getLock(lockName);
    }
}
```

2个线程同时竞争锁，进行测试，先启动 `LockDemo1`  再启动 `LockDemo2`

```java
public class LockDemo1 {

    public static void main(String[] args) throws InterruptedException {
        RLock lock = RedisLockUtil.getLock("lock1");
        try{
            boolean isLocked = lock.tryLock();
            System.out.println(isLocked);
            System.out.println("lock1 is locked in demo1");
            for (int i = 0; i < 20; i++) {
                System.out.println("demo1 do " + i);
                TimeUnit.SECONDS.sleep(1);
            }
        } finally {
            lock.unlock();
            System.out.println("demo1 unlock" + System.currentTimeMillis());
        }
    }
}
```

```java
public class LockDemo2 {

    public static void main(String[] args) throws InterruptedException {
        RLock lock = RedisLockUtil.getLock("lock1");
        try{
            lock.lock();
            System.out.println("lock1 is locked in demo2 " + System.currentTimeMillis());
            for (int i = 0; i < 10; i++) {
                System.out.println("demo2 do " + i);
                TimeUnit.SECONDS.sleep(1);
            }
        } finally {
            lock.unlock();
        }
    }
}
```



## 作业5

### 1)什么是Redis pubsub

传统的redis队列是不支持广播的，为了支持广播，redis新增了一个PubSub的模块，而不是使用传统的5种基本数据类型。

缺点：

1.生产的消息必须马上被消费，如果没有消费者则该消息会被丢弃，即重连的消费者不能接收到挂掉这段时间生产的消息。

2.PubSub的消息不会被持久化，即Redis如果宕机，那么里面的消息会消失。

所以Redis的PubSub没有合适的应用场景，一般都是使用MQ来完成PubSub。

### 2)命令端实现Redis PubSub

启动3个客户端，1个Pub，2个Sub

`Pub`  发布消息命令= publish channelName "message"，返回值表示接受消息的Sub的个数

```shell
PS C:\Users\Administrator> cd E:\Redis-x64-3.0.502
PS E:\Redis-x64-3.0.502> .\redis-cli.exe -h 127.0.0.1 -p 6380
127.0.0.1:6380> publish topic "haha"  # 没有Sub 订阅 topic 所以返回0
(integer) 0
127.0.0.1:6380> publish topic1 "haha"  # 有1个Sub 订阅 topic1，返回1
(integer) 1
127.0.0.1:6380> publish topic1 "xixi"
(integer) 1
127.0.0.1:6380> publish topic1 "cuicui" # 有2个Sub 订阅 topic1，返回2
(integer) 2
```



`Sub1`   在一开始就订阅topic1的消息，能接收全部消息

接收消息命令=subscribe "channelName"

```shell
PS C:\Users\Administrator> cd E:\Redis-x64-3.0.502
PS E:\Redis-x64-3.0.502> .\redis-cli.exe -h 127.0.0.1 -p 6380
127.0.0.1:6380> subscribe topic1
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "topic1"
3) (integer) 1
1) "message"
2) "topic1"
3) "haha"
1) "message"
2) "topic1"
3) "xixi"
1) "message"
2) "topic1"
3) "cuicui"
```

`Sub2`  之后才订阅topic1的消息，只能接收之后的消息

```shell
PS C:\Users\Administrator> cd E:\Redis-x64-3.0.502
PS E:\Redis-x64-3.0.502>  .\redis-cli.exe -h 127.0.0.1 -p 6380
127.0.0.1:6380> subscribe topic1
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "topic1"
3) (integer) 1
1) "message"
2) "topic1"
3) "cuicui"
```

**注意：如果配置了主从，则Pub在master发布的消息，Sub在slave也能收到。但是，Pub的返回值表示的是连接master的Sub数量，如下：**

`Sub3Slave`

```shell
PS E:\Redis-x64-3.0.502> .\redis-cli.exe -h 127.0.0.1 -p 6379
127.0.0.1:6379> subscribe topic1
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "topic1"
3) (integer) 1
1) "message"
2) "topic1"
3) "lisi"
```

### 3)Java实现Redis PubSub

1.引入Jedis依赖

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
```

2.创建 `JedisPubSub` 的子类用来监听Pub，最简单的功能只需要重写onMessage，并自定义处理逻辑

```java
import redis.clients.jedis.JedisPubSub;

public class JedisSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        // diy do something...
        System.out.println("onMessage  Channel:" + channel + ",Message:" + message);
    }

}
```

3.创建 `RedisPub`并作为启动类

```java
public class RedisPub {
    public static void main(String[] args) throws InterruptedException {
        // 分别创建生产者和消费者的客户端，注意不能使用同一个客户端
        Jedis jedisPub = new Jedis("localhost", 6380);
        Jedis jedisSub = new Jedis("localhost", 6380);
        new Thread(()->{ // 另起线程监听消息 到 topic1 channel
            System.out.println("topic1 sub start");
            jedisSub.subscribe(new JedisSub(), "topic1"); // 核心逻辑是do while 监听消息，并调用onMassage来处理消息
            System.out.println("topic1 sub end");
        }).start();
        for (int i = 0; i < 10; i++) { // 发布消息 到topic1 channel
            TimeUnit.SECONDS.sleep(2);
            jedisPub.publish("topic1", "topic1 msg" + i);
        }
        jedisSub.close();
        jedisPub.close();
    }
}
```



# 参考链接

[redis PubSub](https://www.cnblogs.com/longjee/p/8668974.html)

[Java实现RedisTemplate](https://www.cnblogs.com/superfj/p/9232482.html)