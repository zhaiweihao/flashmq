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
public class SendMessageProcessor implements NettyRequestProcessor {

    private final BrokerController brokerController;

    public SendMessageProcessor(final BrokerController brokerController){
        this.brokerController = brokerController;
    }

    public RemotingCommand processRequst(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand response = new RemotingCommand();
        String topic = request.getHeader().getTopic();
        Message msg = RemotingSerializable.decode(request.getBody(), Message.class);
        this.brokerController.putMessage(topic, msg);
        response.setCode(ResponseCode.SUCCESS);

        return response;
    }
}
