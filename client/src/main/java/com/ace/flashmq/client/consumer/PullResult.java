package com.ace.flashmq.client.consumer;

import com.ace.flashmq.common.message.Message;

import java.util.List;

/**
 * @author:ace
 * @date:2020-09-03
 */
public class PullResult {
    private final Message msg;
    private final PullStatus pullStatus;

    public PullResult(PullStatus pullStatus, Message msg) {
        this.msg = msg;
        this.pullStatus = pullStatus;
    }

    public Message getMsg() {
        return msg;
    }

    public PullStatus getPullStatus() {
        return pullStatus;
    }
}
