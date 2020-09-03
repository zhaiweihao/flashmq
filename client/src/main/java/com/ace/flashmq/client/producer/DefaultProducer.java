package com.ace.flashmq.client.producer;

import com.ace.flashmq.client.impl.ClientAPIImpl;
import com.ace.flashmq.client.impl.ClientInstance;
import com.ace.flashmq.client.impl.ClientManager;
import com.ace.flashmq.common.message.Message;
import com.ace.flashmq.remoting.CommandCustomHeader;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class DefaultProducer {

    private ClientInstance clientInstance;

    private String clientId = "default";

    public DefaultProducer() {

    }

    public void start() {
        try {
            clientInstance = ClientManager.getInstance().getOrCreateClientInstance(clientId);
            clientInstance.start();
        } catch (Throwable e) {
            System.out.println("producer start error ...");
        }
    }

    public SendResult send(Message msg) {
        try {
            return sendDefault(msg);
        }catch (Throwable e){
            System.out.println("producer send error ...");
        }
        return null;
    }

    private SendResult sendDefault(Message msg) throws Throwable {
        CommandCustomHeader header = new CommandCustomHeader();
        header.setTopic(msg.getTopic());
        String brokerAddr = this.clientInstance.findBrokerAddr(msg.getTopic());
        SendResult result = this.clientInstance.getClientAPI().sendMessage(brokerAddr, msg, header);

        return result;
    }

    public void addBroker(final String topic, final String brokerAddr) {
        this.clientInstance.addBrokerAddr(topic, brokerAddr);
    }
}
