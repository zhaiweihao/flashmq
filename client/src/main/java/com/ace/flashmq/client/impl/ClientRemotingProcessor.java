package com.ace.flashmq.client.impl;

import com.ace.flashmq.remoting.netty.NettyRequestProcessor;
import com.ace.flashmq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class ClientRemotingProcessor implements NettyRequestProcessor {
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
        return null;
    }
}
