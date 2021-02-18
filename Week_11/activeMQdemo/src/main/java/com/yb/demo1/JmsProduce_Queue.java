package com.yb.demo1;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消息生产者-Queue
 *
 * @auther yb
 * @date 2021/2/6 12:43
 */
public class JmsProduce_Queue {
    public static void main(String[] args) throws JMSException {
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
        producer.send(textMessage);
        // 9、关闭资源。
        producer.close();// 关闭producer
        session.close();// 关闭session
        connection.close();// 关闭connection

    }
}
