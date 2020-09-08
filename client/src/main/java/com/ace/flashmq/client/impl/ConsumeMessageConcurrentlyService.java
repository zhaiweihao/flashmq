package com.ace.flashmq.client.impl;

import com.ace.flashmq.client.consumer.ConsumeMessageServie;
import com.ace.flashmq.client.consumer.listener.MessageListenerConcurrently;
import com.ace.flashmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author:ace
 * @date:2020-09-03
 */
public class ConsumeMessageConcurrentlyService implements ConsumeMessageServie {
    static final Log logger = LogFactory.getLog(ConsumeMessageConcurrentlyService.class);

    private final MessageListenerConcurrently messageListener;
    private final LinkedBlockingQueue<Runnable> consumeRequestQueue;
    private final ThreadPoolExecutor consumeExecutor;

    static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    public ConsumeMessageConcurrentlyService(final MessageListenerConcurrently messageListener) {
        this.messageListener = messageListener;
        consumeRequestQueue = new LinkedBlockingQueue<>();
        ;
        consumeExecutor = new ThreadPoolExecutor(CPU_COUNT, 2 * CPU_COUNT,
                1000 * 60,
                TimeUnit.MILLISECONDS,
                this.consumeRequestQueue,
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "ConsumeMessageThread_" + threadIndex.incrementAndGet());
                    }
                });
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void submitConsumeRequest(Message msg) {
        try {
            this.consumeExecutor.submit(() -> {
                this.messageListener.consumeMessage(msg);
            });
        } catch (Throwable e) {
            logger.error("concurrently consume error ...", e);
        }
    }
}
