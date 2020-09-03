package com.ace.flashmq.client.consumer;

/**
 * @author:ace
 * @date:2020-09-03
 */
public interface PullCallback {
    void onSuccess(final PullResult pullResult);
}
