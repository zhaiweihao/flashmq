package com.ace.flashmq.remoting.netty;

import com.ace.flashmq.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

/**
 * @author:ace
 * @date:2020-09-01
 */
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand, ByteBuf out) {
        try{
            ByteBuffer header = remotingCommand.encodeHeader();
            out.writeBytes(header);
            byte[] body = remotingCommand.getBody();
            if(body != null) {
                out.writeBytes(body);
            }
        }catch (Exception e){

        }
    }
}
