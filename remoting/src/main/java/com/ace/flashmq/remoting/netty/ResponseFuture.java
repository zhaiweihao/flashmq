package com.ace.flashmq.remoting.netty;

import com.ace.flashmq.remoting.protocol.RemotingCommand;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class ResponseFuture {
    private final int opaque;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);
    private volatile RemotingCommand responseCommand;
    private volatile boolean sendRequestOK = true;

    public ResponseFuture(int opaque){
        this.opaque = opaque;
    }

    public int getOpaque() {
        return opaque;
    }

    public RemotingCommand getResponseCommand() {
        return responseCommand;
    }

    public RemotingCommand waitResponse() throws Exception{
        countDownLatch.await(3, TimeUnit.SECONDS);
        return this.responseCommand;
    }

    public void setResponseCommand(RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
        this.countDownLatch.countDown();
    }

    public boolean isSendRequestOK() {
        return sendRequestOK;
    }

    public void setSendRequestOK(boolean sendRequestOK) {
        this.sendRequestOK = sendRequestOK;
    }
}
