package org.vfs.server;

import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.vfs.server.commands.Command;
import org.vfs.server.network.*;
import org.vfs.server.services.UserSessionService;

/**
 * Server class.
 *
 * @author Lipatov Nikita
 */
@Slf4j
@Component
public class Server implements Runnable {

    private final Environment environment;
    private final NetworkConfig networkConfig;
    private final UserSessionService userSessionService;
    private final Map<String, Command> commands;
    private final CommandLine commandLine;

    @Autowired
    public Server(
            Environment environment,
            NetworkConfig networkConfig,
            UserSessionService userSessionService,
            Map<String, Command> commands) {
        this.environment = environment;
        this.networkConfig = networkConfig;
        this.userSessionService = userSessionService;
        this.commands = commands;

        this.commandLine = new CommandLine(commands);

        // W/A: do not start up netty if it's test profile
        if (Optional.of(environment.getActiveProfiles())
                .filter(ps -> ps.length > 0)
                .map(ps -> ps[0])
                .filter(p -> p.equalsIgnoreCase("test"))
                .isEmpty()) {
            run();
        }
    }

    public void run() {
        log.info("Server started.");

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

