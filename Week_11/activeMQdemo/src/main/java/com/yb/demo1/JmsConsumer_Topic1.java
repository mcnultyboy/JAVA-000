package com.yb.demo1;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;

public class JmsConsumer_Topic1 {
    public static void main(String[] args) throws JMSException {
        String brokerURL = "tcp://localhost:61616";
        //1.创建连接工厂,按照指定的url地址,采用默认帐号密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);

        //2.通过连接工厂,获得连接connection
        Connection connection = factory.createConnection();

        //用户wangwu订阅了
        connection.setClientID("Consumer1_topic_yb");
        connection.start();
        //3.创建会话session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4.创建目的地(具体是列队还是主题topic);
        Topic topic = session.createTopic("topic-yb");
        MessageConsumer consumer = session.createConsumer(topic);
        //启动订阅

        consumer.setMessageListener((message) -> {
            if (message instanceof TextMessage) {
                try {
                    String text = ((TextMessage) message).getText();
                    System.out.println("Topic-Consumer1" + text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        //关闭资源
//        session.close();
//        connection.close();
    }
}
