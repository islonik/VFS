package org.vfs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.command.CommandParser;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.*;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.LockService;
import org.vfs.server.services.NodeService;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.UserService;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.vfs.core.network.protocol.Response.*;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
public class CommandLine {
    private static final Logger log = LoggerFactory.getLogger(CommandLine.class);

    Map<String, Runnable> commands = new HashMap<String, Runnable>() {{

        put("cd", new Runnable() {
            @Override
            public void run() {
                CommandValues values = parser.getCommandValues();
                Node directory = userSession.getNode();
                String source = values.getNextParam();

                if (source == null) {
                    source = ".";
                }

                Node node = nodeService.search(directory, source);
                if(node != null) {
                    if (node.getType() == NodeTypes.FILE) {
                        clientWriter.send(newResponse(STATUS_OK, "Source node is file!"));
                    } else {
                        if (node != null) {
                            userSession.setNode(node);
                            clientWriter.send(newResponse(STATUS_OK, nodeService.getFullPath(node)));
                        } else {
                            clientWriter.send(newResponse(STATUS_OK, "Directory wasn't found!"));
                        }
                    }
                } else {
                    clientWriter.send(newResponse(STATUS_OK, "Destination node is not found!"));
                }
            }
        });

        put("connect", new Runnable() {
            @Override
            public void run() {
                if (userService.isLogged(user.getLogin())) {
                    clientWriter.send(newResponse(STATUS_FAIL_CONNECT, "User was registered before with such login already. Please, change the login!"));
                } else {
                    userService.attachUser(userSession.getUser().getId(), user.getLogin());
                    clientWriter.send(newResponse(STATUS_SUCCESS_CONNECT, nodeService.getFullPath(userSession.getNode()), userSession.getUser().getId()));
                }
            }
        });

        put("copy", new Runnable() {
            @Override
            public void run() {
                Node directory = userSession.getNode();
                CommandValues values = parser.getCommandValues();
                String source = values.getNextParam();
                String destination = values.getNextParam();

                Node sourceNode = nodeService.search(directory, source);
                Node destinationNode = nodeService.search(directory, destination);

                if (sourceNode == null) {
                    clientWriter.send(newResponse(STATUS_OK, "Source path/node is not found!"));
                    return;
                }
                if (destinationNode == null) {
                    clientWriter.send(newResponse(STATUS_OK, "Destination path/node is not found!"));
                    return;
                }

                if (destinationNode.getType() == NodeTypes.DIR) {

                    if (lockService.isLocked(destinationNode, true)) {
                        clientWriter.send(newResponse(STATUS_OK, "Node or children nodes is/are locked!"));
                        return;
                    }

                    Node copyNode = nodeService.clone(sourceNode);
                    nodeService.setParent(copyNode, destinationNode);
                    clientWriter.send(
                            newResponse(STATUS_OK,
                                    "Source node " + nodeService.getFullPath(sourceNode) + " was copied to destination node " + nodeService.getFullPath(destinationNode)
                            )
                    );
                } else {
                    clientWriter.send(newResponse(STATUS_OK, "Destination path is not directory"));
                }
            }
        });

        put("help", new Runnable() {
            @Override
            public void run() {
                clientWriter.send(newResponse(STATUS_OK,
                        "You can use next commands:\n" +
                                "    * - cd directory \n" +
                                "    * - connect server_name:port login \n" +
                                "    * - copy node directory \n" +
                                "    * - help \n" +
                                "    * - lock [-r] node \n" +
                                "        -r - enable recursive mode \n" +
                                "    * - mkdir directory \n" +
                                "    * - mkfile file\n" +
                                "    * - move node directory \n" +
                                "    * - print \n" +
                                "    * - quit \n" +
                                "    * - rename node name \n" +
                                "    * - rm node \n" +
                                "    * - unlock [-r] node \n" +
                                "        -r - enable recursive mode \n"
                ));
            }
        });

        put("lock", new Runnable() {
            @Override
            public void run() {
                User user = userSession.getUser();
                Node directory = userSession.getNode();
                CommandValues values = parser.getCommandValues();
                String key = values.getNextKey();
                String lockDirectory = values.getNextParam();

                Node node = nodeService.search(directory, lockDirectory);
                if (node != null) {
                    boolean recursive = false;
                    if(key != null && key.equals("r")) {
                        recursive = true;
                    }
                    if (lockService.isLocked(node, recursive)) {
                        clientWriter.send(newResponse(STATUS_OK, "Node or children nodes is/are locked!"));
                        return;
                    }
                    lockService.lock(user, node, recursive);
                    clientWriter.send(newResponse(STATUS_OK, "Node " + nodeService.getFullPath(node) + " was locked!"));
                } else {
                    clientWriter.send(newResponse(STATUS_OK, "Destination node is not found!"));
                }
            }
        });

        put("mkdir", new Runnable() {
            @Override
            public void run() {
                Node directory = userSession.getNode();
                CommandValues values = parser.getCommandValues();
                String createNode = values.getNextParam();

                Node node = nodeService.search(directory, createNode);
                if (node == null) {
                    node = nodeService.createNode(directory, createNode, NodeTypes.DIR);
                    if(node != null) {
                        clientWriter.send(newResponse(STATUS_OK, "Directory " + nodeService.getFullPath(node) + " was created!"));
                    } else {
                        clientWriter.send(newResponse(STATUS_OK, "Directory was not created!"));
                    }
                } else {
                    clientWriter.send(newResponse(STATUS_OK, "Directory could not be created!"));
                }
            }
        });

        put("mkfile", new Runnable() {
            @Override
            public void run() {
                Node directory = userSession.getNode();
                CommandValues values = parser.getCommandValues();
                String createNode = values.getNextParam();

                Node node = nodeService.search(directory, createNode);
                if (node == null) {
                    node = nodeService.createNode(directory, createNode, NodeTypes.FILE);
                    if(node != null) {
                        clientWriter.send(newResponse(STATUS_OK, "File " + nodeService.getFullPath(node) + " was created!"));
                    } else {
                        clientWriter.send(newResponse(STATUS_OK, "File was not created!"));
                    }
                } else {
                    clientWriter.send(newResponse(STATUS_OK, "File could not be created!"));
                }
            }
        });

        put("move", new Runnable() {
            @Override
            public void run() {
                Node directory = userSession.getNode();
                CommandValues values = parser.getCommandValues();
                String source = values.getNextParam();
                String destination = values.getNextParam();

                Node sourceNode = nodeService.search(directory, source);
                Node destinationNode = nodeService.search(directory, destination);

                if (sourceNode == null) {
                    clientWriter.send(newResponse(STATUS_OK, "Source path/node not found!"));
                    return;
                }

                if (destinationNode == null) {
                    clientWriter.send(newResponse(STATUS_OK, "Destination path/node not found!"));
                    return;
                }

                if (destinationNode.getType() == NodeTypes.DIR) {
                    if (lockService.isLocked(destinationNode, true)) {
                        clientWriter.send(newResponse(STATUS_OK, "Node or children nodes is/are locked!"));
                        return;
                    }

                    Node parent = sourceNode.getParent();
                    nodeService.setParent(sourceNode, destinationNode);
                    nodeService.removeNode(parent, sourceNode);

                    clientWriter.send(newResponse(STATUS_OK,
                            "Source node " + nodeService.getFullPath(sourceNode) + " was moved to destination node " + nodeService.getFullPath(destinationNode)));
                } else {
                    clientWriter.send(newResponse(STATUS_OK, "Destination path is not directory"));
                }
            }
        });

        put("print", new Runnable() {
            @Override
            public void run() {
                Node directory = userSession.getNode();
                clientWriter.send(newResponse(STATUS_OK, nodeService.printTree(directory)));
            }
        });

        put("quit", new Runnable() {
            @Override
            public void run() {
                // TODO: WTF with thread??
                lockService.unlockAllNodes(userSession.getUser());
                userService.stopSession(userSession.getUser().getId());
                clientWriter.send(newResponse(STATUS_SUCCESS_QUIT, "You are disconnected from server!"));
            }
        });

        put("rename", new Runnable() {
            @Override
            public void run() {
                Node directory = userSession.getNode();
                CommandValues values = parser.getCommandValues();
                String renameNode = values.getNextParam();
                String newName = values.getNextParam();

                Node node = nodeService.search(directory, renameNode);
                String oldName = node.getName();

                if (node != null) {
                    if (lockService.isLocked(node, false)) {
                        clientWriter.send(newResponse(STATUS_OK, "Node is locked!"));
                        return;
                    }

                    node.setName(newName);

                    clientWriter.send(newResponse(STATUS_OK, "Node " + oldName + " was renamed to " + newName));
                } else {
                    clientWriter.send(newResponse(STATUS_OK, "Node is not found!"));
                }
            }
        });

        put("rm", new Runnable() {
            @Override
            public void run() {
                Node directory = userSession.getNode();
                CommandValues values = parser.getCommandValues();
                String removeNode = values.getNextParam();

                Node node = nodeService.search(directory, removeNode);

                if (node != null) {
                    if (lockService.isLocked(node, true)) {
                        clientWriter.send(newResponse(STATUS_OK, "Node or children nodes is / are locked!"));
                        return;
                    }

                    Node removedNode = nodeService.removeNode(directory, removeNode);
                    if (removedNode == null) {
                        clientWriter.send(newResponse(STATUS_OK, "Node was found, but wasn't deleted!"));
                    } else {
                        clientWriter.send(newResponse(STATUS_OK, "Node " + removedNode.getName() + " was deleted!"));
                    }
                } else {
                    clientWriter.send(newResponse(STATUS_OK, "Node is not found!"));
                }
            }
        });

        put("unlock", new Runnable() {
            @Override
            public void run() {
                User user = userSession.getUser();
                Node directory = userSession.getNode();
                CommandValues values = parser.getCommandValues();
                String key = values.getNextKey();
                String unlockDirectory = values.getNextParam();

                Node node = nodeService.search(directory, unlockDirectory);
                if (node != null) {
                    boolean recursive = false;
                    if(key != null && key.equals("r")) {
                        recursive = true;
                    }

                    if (!lockService.isLocked(node, recursive)) {
                        clientWriter.send(newResponse(STATUS_OK, "Node is already unlocked!"));
                        return;
                    }
                    if (lockService.unlock(user, node, recursive)) {
                        clientWriter.send(newResponse(STATUS_OK, "Node " + nodeService.getFullPath(node) + " was unlocked!"));
                    } else {
                        clientWriter.send(newResponse(STATUS_OK, "Node is locked by different user!"));
                    }
                } else {
                    clientWriter.send(newResponse(STATUS_OK, "Node is not found!"));
                }
            }
        });
    }};

    private final LockService lockService;
    private final NodeService nodeService;
    private final UserService userService;
    private final UserSession userSession;
    private final ClientWriter clientWriter;
    private final XmlHelper xmlHelper;
    private final CommandParser parser;
    private User user;

    public CommandLine(LockService lockService, NodeService nodeService, UserService userService, UserSession userSession, ClientWriter clientWriter) {
        this.lockService = lockService;
        this.nodeService = nodeService;
        this.userService = userService;
        this.userSession = userSession;
        this.clientWriter = clientWriter;
        xmlHelper = new XmlHelper();
        parser = new CommandParser();
    }

    public void onUserInput(String message) {

        Request request = xmlHelper.unmarshal(Request.class, message);
        user = request.getUser();

        String fullCommand = request.getCommand();
        parser.parse(fullCommand);
        CommandValues commandValues = parser.getCommandValues();

        String command = commandValues.getCommand();

        try {
            if (commands.containsKey(command)) {
                commands.get(command).run();
            } else {
                clientWriter.send(newResponse(STATUS_OK, "No such command! Please check you syntax or type 'help'!"));
            }
        } catch(Exception e) {
            clientWriter.send(newResponse(STATUS_FAIL, e.getMessage()));
        }
    }

}

