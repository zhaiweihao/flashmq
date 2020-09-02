package com.ace.flashmq.common.message;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class Message {
    private String topic;
    private byte[] body;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
