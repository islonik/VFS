package org.vfs.server.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.server.CommandLine;
import org.vfs.server.services.UserSessionService;

/**
 * @author Lipatov Nikita
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel>
{
    private UserSessionService userSessionService;
    private CommandLine commandLine;

    public NettyServerInitializer(UserSessionService userSessionService, CommandLine commandLine) {
        super();
        this.userSessionService = userSessionService;
        this.commandLine = commandLine;
    }

    public void initChannel(SocketChannel ch) throws Exception
    {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(Protocol.Request.getDefaultInstance()));

        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());

        /**
         *
         */
        NettyServerHandler handler = new NettyServerHandler(userSessionService, commandLine);

        pipeline.addLast(new ChannelHandler[] { handler });
    }
}
