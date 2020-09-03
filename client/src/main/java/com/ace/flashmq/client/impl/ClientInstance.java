package com.ace.flashmq.client.impl;

import com.ace.flashmq.client.consumer.DefaultConsumer;
import com.ace.flashmq.client.consumer.PullMessageService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class ClientInstance {
    private final ClientAPIImpl clientAPI;
    private final ConcurrentHashMap<String /* topic */, String /* brokerAddr */> brokerTable = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String /* topic */, DefaultConsumer> consumerTable = new ConcurrentHashMap<>();
    private final PullMessageService pullMessageService;

    public ClientInstance() {
        clientAPI = new ClientAPIImpl();
        pullMessageService = new PullMessageService(this);
    }

    public ClientAPIImpl getClientAPI() {
        return clientAPI;
    }

    public void start() throws Throwable {
        this.clientAPI.start();
        // todo start pullservice
        new Thread(pullMessageService).start();
    }

    /**
     * 检查broker状态
     * 心跳检测
     */
    private void startScheduledTask() {

    }

    public boolean registerConsumer(final String topic, final DefaultConsumer consumer) {
        if (null == topic || null == consumer) {
            return false;
        }
        DefaultConsumer prev = this.consumerTable.putIfAbsent(topic, consumer);
        if (prev != null) {
            return false;
        }
        return true;
    }

    public DefaultConsumer selectConsumer(final String topic) {
        return this.consumerTable.get(topic);
    }

    public PullMessageService getPullMessageService() {
        return pullMessageService;
    }

    public String findBrokerAddr(final String topic){
        return brokerTable.get(topic);
    }

    public void addBrokerAddr(final String topic, final String brokerAddr){
        this.brokerTable.put(topic, brokerAddr);
    }
}
