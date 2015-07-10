package org.vfs.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Lipatov Nikita
 */
public class NettyClient {

    private volatile UserManager userManager;
    private volatile NetworkManager networkManager;
    private volatile MessageSender messageSender;

    public NettyClient(UserManager userManager, NetworkManager networkManager, MessageSender messageSender) {
        this.userManager = userManager;
        this.networkManager = networkManager;
        this.messageSender = messageSender;
    }

    public Channel createChannel(String host, int port) {
        Channel channel = null;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            NettyClientInitializer initializer = new NettyClientInitializer(this.userManager, this.networkManager, this.messageSender);
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(initializer);

            // Start the connection attempt.
            channel = b.connect(host, port).sync().channel();
        } catch(InterruptedException ie) {
            System.err.println(ie);
        }
        return channel;
    }

}
