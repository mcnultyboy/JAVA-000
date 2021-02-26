package com.yb.demo1;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;

public class JmsConsumer_Queue_DLQ {
    public static void main(String[] args) throws JMSException {
        String brokerURL = "tcp://localhost:61616";
        //1.创建连接工厂,按照指定的url地址,采用默认帐号密码
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", brokerURL);

        //2.通过连接工厂,获得连接connection
        Connection connection = factory.createConnection();

        //用户wangwu订阅了
        connection.setClientID("Consumer_Queue_yb_DLQ");
        connection.start();
        //3.创建会话session
//        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Session session = connection.createSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);

        //4.创建目的地(具体是列队还是主题topic);
        // 处理自定义死信队列中的消息
//        Queue queue = session.createQueue("DLQ.queue-yb");
        // 处理默认死信队列中的消息
        // ActiveMQ.DLQ 是对自定义死信队列的兜底，即自定义的死信队列中的消息形成的毒丸最终放到ActiveMQ.DLQ中。如果ActiveMQ.DLQ也消费失败，则该条消息被丢弃
        Queue queue = session.createQueue("ActiveMQ.DLQ");
        MessageConsumer consumer = session.createConsumer(queue);
        //启动订阅

        // 设置listener，用来监听消息
        consumer.setMessageListener((message) -> {
            if (message instanceof TextMessage) {
                try {
                    String text = ((TextMessage) message).getText();
                    System.out.println(text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            //
            throw new RuntimeException();
        });


        //关闭资源
//        session.close();
//        connection.close();
    }
}
