package com.ace.flashmq.remoting.netty;

import com.ace.flashmq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author:ace
 * @date:2020-09-02
 */
public interface NettyRequestProcessor {
    RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Throwable;
}
