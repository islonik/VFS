package org.vfs.client;

import org.vfs.client.network.UserManager;
import org.vfs.client.network.NetworkManager;
import org.vfs.core.command.CommandParser;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Lipatov Nikita
 */
public class CommandLine {
    private final Map<String, Runnable> commands = new HashMap<String, Runnable>() {{
        put("connect", new Runnable() {
            @Override
            public void run() {
                String serverHost = commandValues.getNextParam();
                String serverPort = commandValues.getNextParam();
                String userLogin  = commandValues.getNextParam();

                checkNotNull(serverHost, "ServerHost is null. Fix it!");
                checkNotNull(serverPort, "ServerPort is null. Fix it!");
                checkNotNull(userLogin, "UserLogin is null. Fix it!");
                checkArgument(!userManager.isAuthorized(), "You are already authorized!");

                try {
                    networkManager.openSocket(serverHost, serverPort);

                    // connection was established
                    User user = new User();
                    user.setId("0");
                    user.setLogin(userLogin);

                    userManager.setUser(user);

                    // create request and send it to server
                    networkManager.getMessageSender().send(user, "connect " + user.getLogin());

                } catch (IOException ioe) {
                    System.err.println("CommandLine.IOException.Message=" + ioe.getMessage());
                }
            }
        });

        put("exit", new Runnable() {
            @Override
            public void run() {
                if (userManager.isAuthorized()) {
                    networkManager.getMessageSender().send(userManager.getUser(), "quit");
                }
                System.exit(0); // close all thread and app
            }
        });

        put("quit", new Runnable() {
            @Override
            public void run() {
                checkArgument(userManager.isAuthorized(), "You are not authorized or connection was lost!");
                networkManager.getMessageSender().send(userManager.getUser(), commandValues.getCommand());
            }
        });

        put("default", new Runnable() {
            @Override
            public void run() {
                checkArgument(userManager.isAuthorized(), "Please connect to the server!");
                networkManager.getMessageSender().send(userManager.getUser(), commandValues.getSource());
            }
        });

    }};

    private CommandValues commandValues;
    private final UserManager userManager;
    private final NetworkManager networkManager;

    public CommandLine(UserManager userManager, NetworkManager networkManager) {
        this.userManager = userManager;
        this.networkManager = networkManager;
    }

    public void execute(String args) {
        args = args.trim();

        CommandParser parser = new CommandParser();
        parser.parse(args);

        commandValues = parser.getCommandValues();

        String command = commandValues.getCommand();

        try {
            if (commands.containsKey(command)) {
                commands.get(command).run();
            } else {
                commands.get("default").run();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error : " + e.getMessage());
        }

    }
}
