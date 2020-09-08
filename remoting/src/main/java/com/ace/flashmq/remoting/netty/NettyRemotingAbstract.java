package com.ace.flashmq.remoting.netty;

import com.ace.flashmq.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author:ace
 * @date:2020-09-02
 */
public abstract class NettyRemotingAbstract {
    private static final Log logger = LogFactory.getLog(NettyRemotingAbstract.class);

    protected ConcurrentHashMap<Integer /* opaque*/, ResponseFuture> responseTable = new ConcurrentHashMap<>(64);
    protected HashMap<Integer /* request code */, NettyRequestProcessor> processorTable = new HashMap<Integer, NettyRequestProcessor>();
    protected ExecutorService processRequestThreadPool;
//    protected ExecutorService processResponseThreadPool;

    static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public NettyRemotingAbstract(){
        this.processRequestThreadPool = new ThreadPoolExecutor(CPU_COUNT, 2 * CPU_COUNT,
                60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyRequestProcessor_" + threadIndex.incrementAndGet());
            }
        });
//        this.processResponseThreadPool = new ThreadPoolExecutor(CPU_COUNT, 2 * CPU_COUNT,
//                60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {
//            private AtomicInteger threadIndex = new AtomicInteger(0);
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "NettyResponseProcessor_" + threadIndex.incrementAndGet());
//            }
//        });
    }

    public void processRequestCommand(final ChannelHandlerContext ctx, final RemotingCommand cmd) {
        NettyRequestProcessor processor = processorTable.get(cmd.getCode());
        final int opaque = cmd.getOpaque();
        if(processor != null){
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try{
                        RemotingCallback callback = new RemotingCallback() {
                            @Override
                            public void callback(RemotingCommand response) {
                                response.setFlag(1);
                                response.setOpaque(opaque);
                                try{
                                    ctx.writeAndFlush(response);
                                }catch (Throwable e){
                                    // todo log
                                    System.out.println("process request callback error ...");
                                }
                            }
                        };
                        RemotingCommand response = processor.processRequest(ctx, cmd);
                        callback.callback(response);
                    }catch (Throwable e){
                        logger.error("process request error ...", e);
                    }
                }
            };
            this.processRequestThreadPool.submit(run);
        }
    }

    public void processResponseCommand(final ChannelHandlerContext ctx, final RemotingCommand cmd) {
        final int opaque = cmd.getOpaque();
        final ResponseFuture responseFuture = responseTable.get(opaque);
        if(responseFuture != null){
            responseFuture.setResponseCommand(cmd);
            responseTable.remove(opaque);
        }
    }

    protected RemotingCommand invokeSyncImpl(final Channel channel, final RemotingCommand request) throws Exception{
        final int opaque = request.getOpaque();

        try{
            final ResponseFuture responseFuture = new ResponseFuture(opaque);
            this.responseTable.put(opaque, responseFuture);
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        responseFuture.setSendRequestOK(true);
                        return;
                    } else{
                        responseFuture.setSendRequestOK(false);
                    }
                    responseTable.remove(opaque);
                    responseFuture.setResponseCommand(null);
                }
            });
            RemotingCommand response = responseFuture.waitResponse();
            if(null == response){
                throw new Exception("");
            }
            return response;
        } finally {
            responseTable.remove(opaque);
        }
    }

    public void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand cmd){
        if (cmd.getFlag() == 0) {
            processRequestCommand(ctx, cmd);
        } else {
            processResponseCommand(ctx, cmd);
        }
    }
}
