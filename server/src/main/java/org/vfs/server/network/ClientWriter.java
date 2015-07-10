package org.vfs.server.network;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.Protocol;

/**
 * @author Lipatov Nikita
 */
public class ClientWriter {
    private static final Logger log = LoggerFactory.getLogger(ClientWriter.class);

    private final ChannelHandlerContext ctx;
    private final NettyServerHandler handler;

    public ClientWriter(ChannelHandlerContext ctx, NettyServerHandler handler) {
        this.ctx = ctx;
        this.handler = handler;
    }

    public void send(Protocol.Response response) {
        handler.sendBack(ctx, response);
    }
}
