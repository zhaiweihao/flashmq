package com.ace.flashmq.remoting.netty;

import com.ace.flashmq.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;

/**
 * @author:ace
 * @date:2020-09-01
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {
    public NettyDecoder() {
        super(Integer.MAX_VALUE, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            ByteBuffer buffer = frame.nioBuffer();

            return RemotingCommand.decode(buffer);
        } catch (Exception e) {

        }
        finally {
            if(null != frame){
                frame.release();
            }
        }
        return null;
    }
}
