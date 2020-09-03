package com.ace.flashmq.remoting.netty;

import com.ace.flashmq.remoting.common.RemotingHelper;
import com.ace.flashmq.remoting.protocol.RemotingCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author:ace
 * @date:2020-09-01
 */
public class NettyRemotingClient extends NettyRemotingAbstract {
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroupWorker;
    private final ConcurrentHashMap<String, Channel> channelTable = new ConcurrentHashMap<>();

    public NettyRemotingClient() {
        eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyClientNioWorker_" + threadIndex.incrementAndGet());
            }
        });
    }

    public void start() throws Exception {
        bootstrap.group(eventLoopGroupWorker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new NettyEncoder(),
                                        new NettyDecoder(),
                                        new NettyClientHandler()
                                );
                    }
                });
    }

    public RemotingCommand invokeSync(String addr, RemotingCommand request) throws Throwable {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try{
                RemotingCommand response = this.invokeSyncImpl(channel, request);
                return response;
            }catch (Throwable e){
                // todo log
                System.out.println("invokeSync error ... ");
                throw e;
            }
        } else{
            // todo 关闭channel
            throw new Exception();
        }
    }

    private Channel getAndCreateChannel(final String addr) {
        if (null == addr) {
            // 暂时不做nameserver
            return null;
        }
        Channel channel = channelTable.get(addr);
        if (channel != null) {
            return channel;
        }
        return createChannel(addr);
    }

    private Channel createChannel(String addr){
        try {
            Channel channel = channelTable.get(addr);
            if (channel != null) {
                return channel;
            }
            synchronized (channelTable) {
                channel = channelTable.get(addr);
                if (channel == null) {
                    ChannelFuture channelFuture = this.bootstrap.connect(RemotingHelper.string2SocketAddress(addr)).sync();
                    channel = channelFuture.channel();
                    if (channel != null && channel.isActive()) {
                        channelTable.put(addr, channel);
                    } else {
                        channel = null;
                    }
                }
            }
            return channel;
        }catch (Throwable e){
            System.out.println("create channel error ...");
        }
        return null;
    }


    public void registerProcessor(int requestCode, NettyRequestProcessor processor) {
        this.processorTable.put(requestCode, processor);
    }

    class NettyClientHandler extends SimpleChannelInboundHandler<RemotingCommand> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand cmd) {
            processMessageReceived(ctx, cmd);
        }
    }
}
