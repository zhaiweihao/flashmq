package com.ace.flashmq.client.consumer;

import com.ace.flashmq.common.message.Message;

/**
 * @author:ace
 * @date:2020-09-03
 */
public interface ConsumeMessageServie {
    void start();

    void shutdown();

    void submitConsumeRequest(final Message msg);
}
