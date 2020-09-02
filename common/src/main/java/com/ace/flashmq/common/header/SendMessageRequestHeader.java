package com.ace.flashmq.common.header;

import com.ace.flashmq.remoting.CommandCustomHeader;
import com.ace.flashmq.remoting.protocol.RemotingCommand;
import com.ace.flashmq.remoting.protocol.RemotingSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class SendMessageRequestHeader extends CommandCustomHeader {

    private String topic;
    private Integer queueId;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public static void main(String[] args) {
        RemotingCommand cmd = new RemotingCommand();
        CommandCustomHeader header = new CommandCustomHeader();
        header.setTopic("ace-queue");
        header.setQueueId(1);
        cmd.setHeader(header);
        cmd.setCode(5);
        cmd.setOpaque(2);

        ByteBuf out = Unpooled.buffer();
        ByteBuffer bheader = cmd.encodeHeader();
        out.writeBytes(bheader);
        byte[] body = cmd.getBody();
        if (body != null) {
            out.writeBytes(body);
        }
        out.skipBytes(4);
        ByteBuffer buf = out.nioBuffer();
        RemotingCommand cmd1 = RemotingCommand.decode(buf);
        System.out.println(cmd1.getHeader().getTopic());
        System.out.println(cmd1.getHeader().getQueueId());
        System.out.println(cmd.getCode());
        System.out.println(cmd.getOpaque());
    }
}
