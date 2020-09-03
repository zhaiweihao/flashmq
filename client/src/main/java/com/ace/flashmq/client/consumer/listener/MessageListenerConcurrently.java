package com.ace.flashmq.client.consumer.listener;

import com.ace.flashmq.common.message.Message;

/**
 * @author:ace
 * @date:2020-09-03
 */
public interface MessageListenerConcurrently {
    ConsumeConcurrentlyStatus consumeMessage(final Message msg);
}
