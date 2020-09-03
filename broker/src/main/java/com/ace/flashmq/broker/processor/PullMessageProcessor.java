package com.ace.flashmq.broker.processor;

import com.ace.flashmq.broker.BrokerController;
import com.ace.flashmq.common.message.Message;
import com.ace.flashmq.remoting.netty.NettyRequestProcessor;
import com.ace.flashmq.remoting.protocol.RemotingCommand;
import com.ace.flashmq.remoting.protocol.RemotingSerializable;
import com.ace.flashmq.remoting.protocol.ResponseCode;
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
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Throwable {

        RemotingCommand response = RemotingCommand.createResponse();
        String topic = request.getHeader().getTopic();
        Message msg = brokerController.getMessage(topic);
        response.setCode(ResponseCode.SUCCESS);
        response.setBody(RemotingSerializable.encode(msg));

        return response;
    }
}
