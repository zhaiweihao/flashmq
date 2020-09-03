package com.ace.flashmq.client.consumer;

import com.ace.flashmq.client.consumer.listener.MessageListenerConcurrently;
import com.ace.flashmq.client.impl.ClientInstance;
import com.ace.flashmq.client.impl.ClientManager;
import com.ace.flashmq.client.impl.ConsumeMessageConcurrentlyService;
import com.ace.flashmq.remoting.CommandCustomHeader;

/**
 * @author:ace
 * @date:2020-09-03
 */
public class DefaultConsumer {
    private ClientInstance clientInstance;
    private ConsumeMessageServie consumeMessageServie;
    private MessageListenerConcurrently messageListener;
    private String clientId = "default";

    public void start() {
        try {
            clientInstance = ClientManager.getInstance().getOrCreateClientInstance(clientId);
            clientInstance.start();
            consumeMessageServie = new ConsumeMessageConcurrentlyService(messageListener);
        } catch (Throwable e) {
            System.out.println("consumer start error ...");
        }
    }

    public void executePullRequest(final PullRequest pullRequest) {
        this.clientInstance.getPullMessageService().executePullRequest(pullRequest);
    }

    public void pullMessage(final PullRequest pullRequest) {
        try {
            PullCallback callback = new PullCallback() {
                @Override
                public void onSuccess(PullResult pullResult) {
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            DefaultConsumer.this.consumeMessageServie.submitConsumeRequest(pullResult.getMsg());
                            // 放回继续拉取消息
                            DefaultConsumer.this.executePullRequest(pullRequest);
                            break;
                        default:
                            DefaultConsumer.this.executePullRequest(pullRequest);
                    }
                }
            };
            pullMessageDefault(pullRequest, callback);
        }catch (Throwable e){
            System.out.println("consumer pull message error ...");
        }
    }

    private void pullMessageDefault(final PullRequest pullRequest, final PullCallback callback) throws Throwable{
        CommandCustomHeader header = new CommandCustomHeader();
        header.setTopic(pullRequest.getTopic());
        String brokerAddr = this.clientInstance.findBrokerAddr(pullRequest.getTopic());
        this.clientInstance.getClientAPI().pullMessage(brokerAddr, header, callback);
    }

    public void registerMessageListener(MessageListenerConcurrently listener) {
        this.messageListener = listener;
    }

    public MessageListenerConcurrently getMessageListener() {
        return this.messageListener;
    }

    public void subscribe(String topic) {
        PullRequest request = new PullRequest();
        request.setTopic(topic);
        this.clientInstance.registerConsumer(topic, this);
        this.executePullRequest(request);

    }

    public void addBroker(final String topic, final String brokerAddr) {
        this.clientInstance.addBrokerAddr(topic, brokerAddr);
    }
}
