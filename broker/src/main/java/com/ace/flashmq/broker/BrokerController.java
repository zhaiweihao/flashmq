package com.ace.flashmq.broker;

import com.ace.flashmq.broker.processor.PullMessageProcessor;
import com.ace.flashmq.broker.processor.SendMessageProcessor;
import com.ace.flashmq.common.message.Message;
import com.ace.flashmq.remoting.netty.NettyRemotingServer;
import com.ace.flashmq.remoting.protocol.RequestCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class BrokerController {
    static final Log logger = LogFactory.getLog(BrokerController.class);

    private final NettyRemotingServer remotingServer;
    private final SendMessageProcessor sendMessageProcessor;
    private final PullMessageProcessor pullMessageProcessor;
    private final ExecutorService sendMessageThreadPool;
    private final ExecutorService pullMessageThreadPool;

    final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    // 不做持久化
    private final ConcurrentHashMap<String /* topic */, LinkedBlockingDeque /* message queue */> messageStore = new ConcurrentHashMap<>();

    public BrokerController(){
        remotingServer = new NettyRemotingServer();
        sendMessageProcessor = new SendMessageProcessor(this);
        pullMessageProcessor = new PullMessageProcessor(this);

        sendMessageThreadPool = new ThreadPoolExecutor(CPU_COUNT, 2*CPU_COUNT, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        pullMessageThreadPool = new ThreadPoolExecutor(CPU_COUNT, 2*CPU_COUNT, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    }

    public void start() throws Throwable{
        registerProcessor();
        remotingServer.start();
    }

    public void shutdown(){

    }

    public void registerProcessor(){
        remotingServer.registerProcessor(RequestCode.SEND_MESSAGE, this.sendMessageProcessor);
        remotingServer.registerProcessor(RequestCode.PULL_MESSAGE, this.pullMessageProcessor);
    }

    public void putMessage(final String topic, final Message msg){
        LinkedBlockingDeque<Message> queue = messageStore.get(topic);
        if(queue == null){
            queue = new LinkedBlockingDeque<>();
            LinkedBlockingDeque<Message> origin = messageStore.putIfAbsent(topic, queue);

            if(origin != null){
                queue = origin;
            }else {
                logger.info("create topic success, topicName : " + topic);
            }
        }
        queue.offer(msg);
    }

    public Message getMessage(final String topic) throws Throwable{
        LinkedBlockingDeque<Message> queue = messageStore.get(topic);
        return queue.poll();
    }
}
