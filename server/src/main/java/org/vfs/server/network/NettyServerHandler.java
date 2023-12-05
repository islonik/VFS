package org.vfs.server.network;

import io.netty.channel.*;
import org.vfs.core.VFSConstants;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol.Request;
import org.vfs.core.network.protocol.Protocol.Response;
import org.vfs.server.CommandLine;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.UserSessionService;

/**
 * @author Lipatov Nikita
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {

    private String userId;
    private UserSessionService userSessionService;
    private CommandLine commandLine;

    public NettyServerHandler(UserSessionService userSessionService, CommandLine commandLine) {
        super();
        this.userSessionService = userSessionService;
        this.commandLine = commandLine;
    }

    // Generate and write a response.
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        UserSession userSession;
        if(request.getUser().getId().equals(VFSConstants.NEW_USER)) {
            userSession = userSessionService.startSession(new ClientWriter(ctx, this));

            userId = userSession.getUser().getId();
        } else {
            userSession = userSessionService.getSession(request.getUser().getId());
        }

        userSession.getTimer().updateTime();

        try {
            commandLine.onUserInput(userSession, request); // QuitException can be thrown here
        } catch(QuitException qe) {
            ctx.close();
        }
    }

    public void sendBack(ChannelHandlerContext ctx, Response response) {
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    // Close the connection when an exception is raised.
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //cause.printStackTrace();
        userSessionService.notifyUsers(
                userId,
                String.format(
                        "User '%s' has been disconnected",
                        userSessionService.getSession(userId).getUser().getLogin()
                )
        );
        userSessionService.stopSession(userId);
        ctx.close();
    }
}
