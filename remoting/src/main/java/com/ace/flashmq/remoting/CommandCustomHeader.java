package com.ace.flashmq.remoting;

/**
 * @author:ace
 * @date:2020-09-01
 */
public class CommandCustomHeader {
    private String topic;
    private Integer queueId;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }
}
