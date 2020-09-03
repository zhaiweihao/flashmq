package com.ace.flashmq.remoting.protocol;

import com.ace.flashmq.remoting.CommandCustomHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author:ace
 * @date:2020-09-01
 */
public class RemotingCommand {
    private static AtomicInteger requestId = new AtomicInteger(0);

    private int code;
    private int opaque = requestId.incrementAndGet();
    private int flag = 0;
    private HashMap<Object, Object> extFields = new HashMap<Object, Object>();
    private CommandCustomHeader header;
    private transient byte[] body;


    public static RemotingCommand decode(final ByteBuffer byteBuffer) {
        int length = byteBuffer.limit();
        int oriHeaderLen = byteBuffer.getInt();
        int headerLength = getHeaderLength(oriHeaderLen);
        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);
        RemotingCommand cmd = RemotingSerializable.decode(headerData, RemotingCommand.class);

        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
        }
        cmd.body = bodyData;

        return cmd;
    }

    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    public ByteBuffer encodeHeader() {
        return encodeHeader(this.body != null ? this.body.length : 0);
    }

    public ByteBuffer encodeHeader(final int bodyLength) {
        int length = 4;
        byte[] headerData;
        headerData = this.headerEncode();
        length += headerData.length;
        length += bodyLength;
        ByteBuffer result = ByteBuffer.allocate(4 + length - bodyLength);
        result.putInt(length);
        result.put(markProtocolType(headerData.length));
        result.put(headerData);
        result.flip();
        return result;
    }

    public static byte[] markProtocolType(int source) {
        byte[] result = new byte[4];

        result[0] = (byte) 0;
        result[1] = (byte) ((source >> 16) & 0xFF);
        result[2] = (byte) ((source >> 8) & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
    }

    public static RemotingCommand createResponse() {
        RemotingCommand result = new RemotingCommand();
        result.setFlag(1);
        return result;
    }

    private byte[] headerEncode() {
        return RemotingSerializable.encode(this);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public HashMap<Object, Object> getExtFields() {
        return extFields;
    }

    public void setExtFields(HashMap<Object, Object> extFields) {
        this.extFields = extFields;
    }

    public CommandCustomHeader getHeader() {
        return header;
    }

    public void setHeader(CommandCustomHeader header) {
        this.header = header;
    }

    public byte[] getBody() {
        return this.body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
