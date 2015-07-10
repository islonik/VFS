package org.vfs.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.Protocol;

/**
 * @author Lipatov Nikita
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Protocol.Response> {

    private volatile UserManager userManager;
    private volatile NetworkManager networkManager;
    private volatile MessageSender messageSender;

    public NettyClientHandler(UserManager userManager, NetworkManager networkManager, MessageSender messageSender) {
        super();
        this.userManager = userManager;
        this.networkManager = networkManager;
        this.messageSender = messageSender;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Protocol.Response response) throws Exception {
        Protocol.Response.ResponseType code = response.getCode();

        String message = response.getMessage();
        Protocol.User user = userManager.getUser();

        switch (code) {
            case SUCCESS_CONNECT:  // success authorization
                user = Protocol.User.newBuilder()
                        .setId(response.getSpecificCode())
                        .setLogin(user.getLogin())
                        .build();
                userManager.setUser(user);
                System.out.println(message);
                break;
            case FAIL_CONNECT:     // fail authorization
                System.err.println(message);
                throw new QuitException("Such user already exist!");
            case SUCCESS_QUIT:     // quit response
                System.out.println(message);
                throw new QuitException("Closing connection by client request");
            case FAIL:
                System.err.println(message);
                break;
            default:
                System.out.println(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        userManager.setUser(null);
        networkManager.closeSocket();
        messageSender.setChannel(null);

        boolean printStackTrace = false;
        boolean printMessage = false;

        if (!(cause instanceof QuitException)) {
            printStackTrace = true;
            printMessage = true;
        }
        if (cause.getMessage().trim().equals("An existing connection was forcibly closed by the remote host")) {
            printStackTrace = false;
        }

        if (printStackTrace) {
            cause.printStackTrace();
        }
        if (printMessage) {
            System.err.println(cause.getLocalizedMessage());
        }

        ctx.close();
    }
}
