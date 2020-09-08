package com.ace.flashmq.example;

import com.ace.flashmq.client.consumer.DefaultConsumer;
import com.ace.flashmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.ace.flashmq.client.consumer.listener.MessageListenerConcurrently;
import com.ace.flashmq.common.message.Message;

/**
 * @author:ace
 * @date:2020-09-03
 */
public class ConsumerExample {

    static final String BROKER = "localhost:8989";
    static final String TOPIC = "test-topic";

    public static void main(String[] args) {
        DefaultConsumer consumer = new DefaultConsumer();

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(Message msg) {
                System.out.println(new String(msg.getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        consumer.addBroker(TOPIC, BROKER);
        consumer.subscribe(TOPIC);
    }
}
