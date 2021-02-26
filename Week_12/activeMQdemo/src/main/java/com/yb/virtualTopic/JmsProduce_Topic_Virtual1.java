package com.yb.virtualTopic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * topic produce
 *
 * @auther yb
 * @date 2021/2/6 17:44
 */
public class JmsProduce_Topic_Virtual1 {

    public static void main(String[] args) {
        try {

            ActiveMQConnectionFactory factoryA = new ActiveMQConnectionFactory(
                    "tcp://127.0.0.1:61616");

            ActiveMQConnection conn = (ActiveMQConnection) factoryA.createConnection();
            conn.start();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            final AtomicInteger aint1 = new AtomicInteger(0);
            MessageListener listenerA = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println(aint1.incrementAndGet()
                                + " => receive from "+ getVirtualTopicConsumerNameA() +": " + ((TextMessage)message).getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Queue queue = new ActiveMQQueue(getVirtualTopicConsumerNameA());
            MessageConsumer consumer1 = session.createConsumer( queue );
            MessageConsumer consumer2 = session.createConsumer( queue );
            consumer1.setMessageListener(listenerA);
            consumer2.setMessageListener(listenerA);
            final AtomicInteger aint2 = new AtomicInteger(0);
            MessageListener listenerB = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println(aint2.incrementAndGet()
                                + " => receive from "+ getVirtualTopicConsumerNameB() +": " + ((TextMessage)message).getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            MessageConsumer consumer3 = session.createConsumer( new ActiveMQQueue(getVirtualTopicConsumerNameB()) );
            consumer3.setMessageListener(listenerB);

            MessageProducer producer = session.createProducer(new ActiveMQTopic(getVirtualTopicName()));
            int index = 0;
            while (index++ < 100) {
                TextMessage message = session.createTextMessage(index
                        + " message.");
                producer.send(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static String getVirtualTopicName() {
        return "VirtualTopic.TEST";
    }

    protected static String getVirtualTopicConsumerNameA() {
        return "Consumer.A.VirtualTopic.TEST";
    }

    protected static String getVirtualTopicConsumerNameB() {
        return "Consumer.B.VirtualTopic.TEST";
    }

}
