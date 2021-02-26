package com.yb.virtualTopic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;

/**
 * topic produce
 *
 * @auther yb
 * @date 2021/2/6 17:44
 */
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
