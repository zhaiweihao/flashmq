package com.ace.flashmq.client.consumer;

import com.ace.flashmq.client.impl.ClientInstance;

import java.util.concurrent.*;

/**
 * @author:ace
 * @date:2020-09-03
 */
public class PullMessageService implements Runnable {

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
        }
    }

    public void run() {
        while (true) {
            try {
                PullRequest request = this.pullRequestQueue.take();
                this.pullMessage(request);
            } catch (Throwable e) {
                System.out.println("pull message service run error ...");
            }
        }
    }
}
