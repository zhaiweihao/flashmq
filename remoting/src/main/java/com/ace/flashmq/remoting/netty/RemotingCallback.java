package com.ace.flashmq.remoting.netty;

import com.ace.flashmq.remoting.protocol.RemotingCommand;

/**
 * @author:ace
 * @date:2020-09-02
 */
public interface RemotingCallback {
    void callback(RemotingCommand cmd);
}
