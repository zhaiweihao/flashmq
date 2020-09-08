package com.ace.flashmq.remoting.netty;

import com.ace.flashmq.remoting.protocol.RemotingCommand;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author:ace
 * @date:2020-09-01
 */
public class NettyRemotingServer extends NettyRemotingAbstract {
    static final Log logger = LogFactory.getLog(NettyRemotingServer.class);

    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup eventLoopGroupBoss;
    private final EventLoopGroup eventLoopGroupSelector;

    public NettyRemotingServer() {
        this.serverBootstrap = new ServerBootstrap();
        if (useEpoll()) {
            eventLoopGroupBoss = new EpollEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, "NettyEpollBoss" + this.threadIndex.incrementAndGet());
                }
            });
            eventLoopGroupSelector = new EpollEventLoopGroup(3, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, "NettyEpollSelector" + this.threadIndex.incrementAndGet());
                }
            });
        } else {
            eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, "NettyNioBoss" + this.threadIndex.incrementAndGet());
                }
            });
            eventLoopGroupSelector = new NioEventLoopGroup(3, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, "NettyNioSelector" + this.threadIndex.incrementAndGet());
                }
            });
        }
    }

    public void start() throws Exception {
        ChannelFuture sync = this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .localAddress(new InetSocketAddress(8989))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new NettyEncoder())
                                .addLast(new NettyDecoder())
                                .addLast(new NettyServerHandler());
                    }
                })
                .bind().sync();
    }

    private boolean useEpoll() {
        String OS_NAME = System.getProperty("os.name");
        boolean isLinux = false;
        if (OS_NAME != null && OS_NAME.toLowerCase().contains("linux")) {
            isLinux = true;
        }
        return isLinux && Epoll.isAvailable();
    }

    public void registerProcessor(int requestCode, NettyRequestProcessor processor){
        this.processorTable.put(requestCode, processor);
    }

    class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand cmd) {
            // todo 处理消息
            processMessageReceived(ctx, cmd);
        }
    }
}
