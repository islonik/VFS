package org.vfs.server;

import java.io.*;
import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.server.commands.Command;
import org.vfs.server.network.*;
import org.vfs.server.services.UserSessionService;

/**
 * Server class.
 *
 * @author Lipatov Nikita
 */
@Component
public class Server implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final NetworkConfig networkConfig;
    private final UserSessionService userSessionService;
    private final Map<String, Command> commands;
    private final CommandLine commandLine;

    @Autowired
    public Server(NetworkConfig networkConfig, UserSessionService userSessionService, Map<String, Command> commands) throws IOException {
        this.networkConfig = networkConfig;
        this.userSessionService = userSessionService;
        this.commands = commands;

        this.commandLine = new CommandLine(commands);

        String out = "Server has been running!";

        System.out.println(out);
        log.info(out);

        run();
    }

    public void run() {
        String address = networkConfig.getAddress();
        int port = networkConfig.getPort();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        NettyServerInitializer initializer = new NettyServerInitializer(userSessionService, commandLine);

        try {
            ServerBootstrap b = new ServerBootstrap();
            ((ServerBootstrap)((ServerBootstrap)b
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class))
                    .handler(new LoggingHandler(LogLevel.INFO)))
                    .childHandler(initializer);

            b.bind(address, port).sync().channel().closeFuture().sync();
        } catch (Throwable e) {
            log.error("Server failure", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

