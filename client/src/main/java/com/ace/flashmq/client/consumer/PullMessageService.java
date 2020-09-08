package com.ace.flashmq.client.consumer;

import com.ace.flashmq.client.impl.ClientInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

/**
 * @author:ace
 * @date:2020-09-03
 */
public class PullMessageService implements Runnable {
    static final Log logger = LogFactory.getLog(PullMessageService.class);

    private final ClientInstance clientInstance;
    private final LinkedBlockingQueue<PullRequest> pullRequestQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        public Thread newThread(Runnable r) {
            return new Thread(r, "PullMessageServiceThread");
        }
    });

    public PullMessageService(ClientInstance clientInstance) {
        this.clientInstance = clientInstance;
    }

    public void executePullRequest(PullRequest request) {
        try {
            this.pullRequestQueue.put(request);
        } catch (InterruptedException e) {
        }
    }

    private void pullMessage(final PullRequest request) {
        final DefaultConsumer consumer = this.clientInstance.selectConsumer(request.getTopic());
        if (consumer != null) {
            consumer.pullMessage(request);
        } else {
            // todo log
            logger.info("no consumer for topic " + request.getTopic());
        }
    }

    public void run() {
        while (true) {
            try {
                PullRequest request = this.pullRequestQueue.take();
                this.pullMessage(request);
            } catch (Throwable e) {
                logger.error("pull message service run error ...", e);
            }
        }
    }
}
