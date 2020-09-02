package com.ace.flashmq.remoting.common;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author:ace
 * @date:2020-09-02
 */
public class RemotingHelper {
    public static SocketAddress string2SocketAddress(final String addr) {
        int split = addr.lastIndexOf(":");
        String host = addr.substring(0, split);
        String port = addr.substring(split + 1);
        InetSocketAddress isa = new InetSocketAddress(host, Integer.parseInt(port));
        return isa;
    }
}
