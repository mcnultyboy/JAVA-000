学习笔记

# day21

- [x] 1、（选做）命令行下练习操作Redis的各种基本数据结构和命令。 

- [x] 2、（选做）分别基于jedis，RedisTemplate，Lettuce，Redission实现redis基本操作 

的demo，可以使用spring-boot集成上述工具。 

- [ ] 3、（选做）spring集成练习: 

  - [ ] 1）实现update方法，配合@CachePut 

  - [ ] 2）实现delete方法，配合@CacheEvict 

  - [ ] 3）将示例中的spring集成Lettuce改成jedis或redisson。 
- [x] 4、（必做）基于Redis封装分布式数据操作： 
- [ ] 1）在Java中实现一个简单的分布式锁； 
  
- [ ] 2）在Java中实现一个分布式计数器，模拟减库存。 
- [x] 5、基于Redis的PubSub实现订单异步处理

作业连接：[day21 homework](https://github.com/mcnultyboy/JAVA-000/tree/main/Week_11/homework21)

# day22

- [x] 1、（必做）配置redis的主从复制，sentinel高可用，Cluster集群。 提交如下内容到github： 

  - [ ] 1）config配置文件， 
- [ ] 2）启动和操作、验证集群下数据读写的命令步骤。 

作业连接：[day22 homework](https://github.com/mcnultyboy/JAVA-000/tree/main/Week_11/08cache/redis)

- [ ] 2、（选做）练习示例代码里下列类中的作业题： 

08cache/redis/src/main/java/io/kimmking/cache/RedisApplication.java 

