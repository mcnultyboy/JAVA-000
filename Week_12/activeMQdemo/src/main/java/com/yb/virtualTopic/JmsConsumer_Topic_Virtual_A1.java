package com.yb.virtualTopic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;

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
