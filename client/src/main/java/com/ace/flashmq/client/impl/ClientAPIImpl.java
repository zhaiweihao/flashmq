package com.ace.flashmq.client.impl;

import com.ace.flashmq.remoting.netty.NettyRemotingClient;
import com.ace.flashmq.remoting.protocol.RequestCode;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class ClientAPIImpl {
    private final NettyRemotingClient remotingClient;

    public ClientAPIImpl(){
        remotingClient = new NettyRemotingClient();

//        remotingClient.registerProcessor(RequestCode.SEND_MESSAGE, );
    }
}
