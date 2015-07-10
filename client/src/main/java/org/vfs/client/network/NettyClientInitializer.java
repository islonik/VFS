package org.vfs.client.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.vfs.core.network.protocol.Protocol;

/**
 * @author Lipatov Nikita
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private volatile UserManager userManager;
    private volatile NetworkManager networkManager;
    private volatile MessageSender messageSender;

    public NettyClientInitializer(UserManager userManager, NetworkManager networkManager, MessageSender messageSender) {
        super();
        this.userManager = userManager;
        this.networkManager = networkManager;
        this.messageSender = messageSender;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(Protocol.Response.getDefaultInstance()));

        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());

        // and then business logic.
        NettyClientHandler handler = new NettyClientHandler(this.userManager, this.networkManager, this.messageSender);
        pipeline.addLast(handler);
    }
}
