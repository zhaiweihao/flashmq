package com.ace.flashmq.broker.processor;

import com.ace.flashmq.broker.BrokerController;
import com.ace.flashmq.remoting.netty.NettyRequestProcessor;
import com.ace.flashmq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class PullMessageProcessor implements NettyRequestProcessor {
    private final BrokerController brokerController;

    public PullMessageProcessor(final BrokerController brokerController){
        this.brokerController = brokerController;
    }
    public RemotingCommand processRequst(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        return null;
    }
}
