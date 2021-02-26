package com.yb.demo1;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;
import java.util.concurrent.*;

public class JmsConsumer_Topic2 {
    public static void main(String[] args) throws JMSException {
        String brokerURL = "tcp://localhost:61616";
        //1.创建连接工厂,按照指定的url地址,采用默认帐号密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);

        //2.通过连接工厂,获得连接connection
        Connection connection = factory.createConnection();

        //用户wangwu订阅了
        connection.setClientID("Consumer2_topic_yb");
        connection.start();
        //3.创建会话session
        // INDIVIDUAL_ACKNOWLEDGE ack模式，每条消息需要consumer确认，如果listener抛出异常，则会重发所有消息，每条消息重发次数次数为6
        Session session = connection.createSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);

        //4.创建目的地(具体是列队还是主题topic);
        Topic topic = session.createTopic("topic-yb");
        MessageConsumer consumer = session.createConsumer(topic);
        //启动订阅

        consumer.setMessageListener((message) -> {
            if (message instanceof TextMessage) {
                try {
                    String text = ((TextMessage) message).getText();
                    System.out.println("Topic-Consumer2" + text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException();
            }
        });

//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 10, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
//        while (true) {
//            Message message = consumer.receive();
//            String text = ((TextMessage) message).getText();
//            threadPoolExecutor.execute(()->{
//                System.out.println("Topic-Consumer2" + text);
//                new RuntimeException();
//            });
//        }

        //关闭资源
//        session.close();
//        connection.close();
    }
}
