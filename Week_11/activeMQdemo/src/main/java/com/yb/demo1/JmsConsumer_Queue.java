package com.yb.demo1;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;

public class JmsConsumer_Queue {
    public static void main(String[] args) throws JMSException {
        String brokerURL = "tcp://localhost:61616";
        //1.创建连接工厂,按照指定的url地址,采用默认帐号密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);

        //2.通过连接工厂,获得连接connection
        Connection connection = factory.createConnection();

        //用户wangwu订阅了
        connection.setClientID("Consumer_Queue_yb");
        connection.start();
        //3.创建会话session
//        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Session session = connection.createSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);

        //4.创建目的地(具体是列队还是主题topic);
        Queue queue = session.createQueue("queue-yb");
        MessageConsumer consumer = session.createConsumer(queue);
        //启动订阅

//        while (true) {
//            Message message = consumer.receive();
//            System.out.println(message);
//        }
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


        //关闭资源
//        session.close();
//        connection.close();
    }
}
