package com.ace.flashmq.client.impl;

import com.ace.flashmq.client.consumer.PullCallback;
import com.ace.flashmq.client.consumer.PullResult;
import com.ace.flashmq.client.consumer.PullStatus;
import com.ace.flashmq.client.producer.SendResult;
import com.ace.flashmq.client.producer.SendStatus;
import com.ace.flashmq.common.message.Message;
import com.ace.flashmq.remoting.CommandCustomHeader;
import com.ace.flashmq.remoting.netty.NettyRemotingClient;
import com.ace.flashmq.remoting.protocol.RemotingCommand;
import com.ace.flashmq.remoting.protocol.RemotingSerializable;
import com.ace.flashmq.remoting.protocol.RequestCode;
import com.ace.flashmq.remoting.protocol.ResponseCode;

import java.util.List;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class ClientAPIImpl {
    private final NettyRemotingClient remotingClient;

    public ClientAPIImpl() {
        remotingClient = new NettyRemotingClient();

//        remotingClient.registerProcessor(RequestCode.SEND_MESSAGE, );
    }

    public void start() throws Throwable {
        remotingClient.start();
    }

    public SendResult sendMessage(final String addr, final Message msg, final CommandCustomHeader header) throws Throwable {
        RemotingCommand request = new RemotingCommand();
        request.setHeader(header);
        request.setCode(RequestCode.SEND_MESSAGE);
        request.setBody(RemotingSerializable.encode(msg));
        RemotingCommand response = remotingClient.invokeSync(addr, request);
        SendStatus sendStatus;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS:
                sendStatus = SendStatus.SEND_OK;
                break;
            default:
                sendStatus = SendStatus.SEND_ERROR;
        }
        SendResult sendResult = new SendResult();
        sendResult.setSendStatus(sendStatus);
        return sendResult;
    }

    public void pullMessage(final String addr, final CommandCustomHeader header, final PullCallback callback) throws Throwable {
        RemotingCommand request = new RemotingCommand();
        request.setHeader(header);
        request.setCode(RequestCode.PULL_MESSAGE);
        RemotingCommand response = remotingClient.invokeSync(addr, request);
        PullStatus pullStatus;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS:
                pullStatus = PullStatus.FOUND;
                break;
            default:
                pullStatus = PullStatus.ERROR;
        }
        Message msg = RemotingSerializable.decode(response.getBody(), Message.class);
        PullResult pullResult = new PullResult(pullStatus, msg);
        callback.onSuccess(pullResult);
    }
}
