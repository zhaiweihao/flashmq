package com.ace.flashmq.example;

import com.ace.flashmq.client.producer.DefaultProducer;
import com.ace.flashmq.client.producer.SendResult;
import com.ace.flashmq.client.producer.SendStatus;
import com.ace.flashmq.common.message.Message;

import java.nio.charset.Charset;

/**
 * @author:ace
 * @date:2020-09-03
 */
public class ProducerExample {
    static final String BROKER = "localhost:8989";
    static final String TOPIC = "test-topic";

    public static void main(String[] args) {
        DefaultProducer producer = new DefaultProducer();
        producer.start();
        producer.addBroker(TOPIC, BROKER);
        for (int i = 0; i < 10; i++) {
            Message msg = new Message();
            msg.setTopic(TOPIC);
            msg.setBody(("this is body" + i).getBytes(Charset.forName("UTF-8")));
            SendResult result = producer.send(msg);
            if (result.getSendStatus().equals(SendStatus.SEND_OK)) {
                System.out.println("success");
            } else {
                System.out.println("error");
            }
        }
    }
}
