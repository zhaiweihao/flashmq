package com.ace.flashmq.client.producer;

/**
 * @author:ace
 * @date:2020-09-03
 */
public class SendResult {
    private SendStatus sendStatus;

    public SendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }
}
