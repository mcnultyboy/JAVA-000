package com.yb.demo1;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;

/**
 * topic produce
 *
 * @auther yb
 * @date 2021/2/6 17:44
 */
public class JmsProduce_Topic {
    public static void main(String[] args) throws JMSException {
        // 1.创建连接工厂，并使用指定的ip和activeMQ的tcp port
        String brokerURL = "tcp://localhost:61616";
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);
        // 2.使用工厂创建连接
        Connection connection = factory.createConnection();
        // 3.开启mq连接
        connection.start();
        // 4.创建session,
//        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);// 不使用事务, 接收方自动ack
        Session session = connection.createSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);// 不使用事务, 接收方自动ack
        // 5.创建destination,topic(queue)
        // 6.创建creater
        String topicName = "topic-yb";
        Topic topic1 = session.createTopic(topicName);
        MessageProducer producer = session.createProducer(topic1);
        // 创建message并发送
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        producer.setPriority(4);
        producer.setTimeToLive(1000 * 60 * 10); // 存储时长ms
        for (int i = 0; i < 5; i++) {
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(topicName + "====" +i);
            producer.send(textMessage, DeliveryMode.PERSISTENT, 4, 1000*5);
            System.out.println("send" + i);
        }
        // 9、关闭资源。
        producer.close();// 关闭producer
        session.close();// 关闭session
        connection.close();// 关闭connection
    }
}
