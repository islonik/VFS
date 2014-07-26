package org.vfs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.command.AbstractCommandLine;
import org.vfs.core.command.Command;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.User;
import org.vfs.core.model.Context;
import org.vfs.server.command.ServerMapping;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeService;
import org.vfs.server.user.UserCell;
import org.vfs.server.user.UserSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Lipatov Nikita
 */
public class CommandLine extends AbstractCommandLine
{
    private static final Logger log = LoggerFactory.getLogger(CommandLine.class);

    public static final String NO_SUCH_COMMAND = "Such command doesn't exist! Please use help command for getting full list of available commands!";

    final Map<String, Runnable> commands = new HashMap<String, Runnable>() {{
        put("cd", new Runnable() {
            @Override
            public void run() {
                User user = context.getUser();
                Directory directory = (Directory)user.getDirectory();

                CommandValues values = context.getCommandValues();
                String source = values.getNextParam();

                if(source == null)
                {
                    source = ".";
                }

                Node node = search(directory, source);
                if(node instanceof File)
                {
                    context.setErrorMessage("Source node is file!");
                }
                else
                {
                    if(node != null)
                    {
                        user.setDirectory((Directory)node);
                        context.setCommandWasExecuted(true);
                        context.setMessage(node.getFullPath());
                    }
                    else
                    {
                        context.setErrorMessage("Directory wasn't found!");
                    }
                }
            }
        });

        put("connect", new Runnable() {
            @Override
            public void run() {
                CommandValues values = context.getCommandValues();
                String userName = values.getNextParam();

                if(UserSession.getInstance().getUser(userName) != null)
                {
                    context.setCode(Response.STATUS_FAIL_CONNECT);
                    context.setErrorMessage("User with such login already was registered before. Please, change the login!");
                    context.setExit(true);
                    return;
                }

                if(UserSession.getInstance().addUser(userName))
                {
                    User user = UserSession.getInstance().getUser(userName);
                    context.setUser(user);
                    context.setCommandWasExecuted(true);
                    context.setBroadcastCommand(true);
                    context.setCode(Response.STATUS_SUCCESS_CONNECT);
                    context.setSpecificCode(Long.parseLong(user.getId()));
                    context.setMessage(((Directory)user.getDirectory()).getFullPath());
                }
                else
                {
                    context.setCode(Response.STATUS_FAIL_CONNECT);
                    context.setErrorMessage("User wasn't added and registered! Try to change user login and type connect command again!");
                    context.setExit(true);
                }
            }
        });

        put("copy", new Runnable() {
            @Override
            public void run() {
                User user = context.getUser();
                Directory directory  = (Directory)user.getDirectory();
                CommandValues values = context.getCommandValues();
                String source        = values.getNextParam();
                String destination   = values.getNextParam();

                Node sourceNode      = search(directory, source);
                Node destinationNode = search(directory, destination);

                if(sourceNode == null)
                {
                    context.setErrorMessage("Source path/node not found!");
                    return;
                }

                if(destinationNode == null)
                {
                    context.setErrorMessage("Destination path/node not found!");
                    return;
                }

                if(destinationNode instanceof Directory)
                {
                    Node copyNode = sourceNode.copy();
                    ((Directory) destinationNode).addNode(copyNode);
                    context.setCommandWasExecuted(true);
                    context.setBroadcastCommand(true);
                    context.setMessage("Source node " + sourceNode.getFullPath() + " was copied to destination node " + destinationNode.getFullPath() );
                }
                else
                {
                    context.setErrorMessage("Destination path is not directory");
                }
            }
        });

        put("help", new Runnable() {
            @Override
            public void run() {
                context.setCommandWasExecuted(true);
                context.setMessage(
                        "You can use next commands:\n" +
                        "    * - connect server_name:port login \n" +
                        "    * - quit \n" +
                        "    * - cd directory \n" +
                        "    * - copy node directory \n" +
                        "    * - help \n" +
                        "    * - lock node \n" +
                        "    * - mkdir directory \n" +
                        "    * - mkfile file\n" +
                        "    * - move node directory \n" +
                        "    * - print \n" +
                        "    * - rm node \n" +
                        "    * - unlock node ");
            }
        });

        put("lock", new Runnable() {
            @Override
            public void run() {
                User user = context.getUser();
                Directory directory = (Directory)user.getDirectory();
                CommandValues values = context.getCommandValues();
                String lockDirectory = values.getNextParam();

                Node node = search(directory, lockDirectory);
                if(node != null)
                {
                    node.setLock(user, true);
                    if(node.isLock(user))
                    {
                        context.setCommandWasExecuted(true);
                        context.setBroadcastCommand(true);
                        context.setMessage("Node " + node.getFullPath() + " was locked!");
                        return;
                    }
                    else
                    {
                        context.setErrorMessage("Node not locked!");
                        return;
                    }
                }
                context.setErrorMessage("Node not found!");
            }
        });

        put("mkdir", new Runnable() {
            @Override
            public void run() {
                User user = context.getUser();
                Directory directory = (Directory)user.getDirectory();
                CommandValues values = context.getCommandValues();
                String createDirectory = values.getNextParam();

                Node node = search(directory, createDirectory);
                if(node == null)
                {
                    Directory makeDirectory = NodeService.getFactory().createDirectory(directory, createDirectory);
                    if(makeDirectory != null)
                    {
                        context.setCommandWasExecuted(true);
                        context.setBroadcastCommand(true);
                        context.setMessage("Directory " + makeDirectory.getFullPath() + " was created!");
                        return;
                    }
                }
                context.setErrorMessage("Directory could not be created!");
            }
        });

        put("mkfile", new Runnable() {
            @Override
            public void run() {
                User user = context.getUser();
                Directory directory = (Directory)user.getDirectory();
                CommandValues values = context.getCommandValues();
                String createFile = values.getNextParam();

                Node node = search(directory, createFile);
                if(node == null)
                {
                    File makeFile = NodeService.getFactory().createFile(directory, createFile);
                    if(makeFile != null)
                    {
                        context.setCommandWasExecuted(true);
                        context.setBroadcastCommand(true);
                        context.setMessage("File " + makeFile.getFullPath() + " was created!");
                        return;
                    }
                }
                context.setErrorMessage("File could not be created!");
            }
        });

        put("move", new Runnable() {
            @Override
            public void run() {
                User user = context.getUser();
                Directory directory = (Directory)user.getDirectory();
                CommandValues values = context.getCommandValues();
                String source      = values.getNextParam();
                String destination = values.getNextParam();

                Node sourceNode      = search(directory, source);
                Node destinationNode = search(directory, destination);

                if(sourceNode == null)
                {
                    context.setErrorMessage("Source path/node not found!");
                    return;
                }

                if(destinationNode == null)
                {
                    context.setErrorMessage("Destination path/node not found!");
                    return;
                }

                if(destinationNode instanceof Directory)
                {
                    Directory parent = sourceNode.getParent();
                    ((Directory) destinationNode).addNode(sourceNode);
                    parent.removeNode(sourceNode);
                    context.setCommandWasExecuted(true);
                    context.setBroadcastCommand(true);
                    context.setMessage("Source node " + sourceNode.getFullPath() + " was moved to destination node " + destinationNode.getFullPath());
                }
                else
                {
                    context.setErrorMessage("Destination path is not directory");
                }
            }
        });

        put("print", new Runnable() {
            @Override
            public void run() {
                simplePrintTreeNode(parentDir, textTree, deep);

                deep++;

                List<Directory> directories = parentDir.getDirectories();
                for (Directory directory : directories)
                {
                    simplePrintTreeDir(directory, textTree, deep);
                }

                List<File> files = parentDir.getFiles();
                for (File file : files)
                {
                    simplePrintTreeNode(file, textTree, deep);
                }

                return textTree;
            }
        });

        put("quit", new Runnable() {
            @Override
            public void run() {
                User user = context.getUser();

                if(UserSession.getInstance().removeUser(user.getId(), user.getLogin()))
                {
                    //context.setUser(null);
                    context.setCode(Response.STATUS_SUCCESS_QUIT); // quit
                    context.setCommandWasExecuted(true);
                    context.setBroadcastCommand(true);
                    context.setExit(true);
                    context.setMessage("You are disconnected from server!");
                }
                else
                {
                    context.setCode(Response.STATUS_FAIL_QUIT);
                    context.setErrorMessage("User wasn't removed! Please try again!");
                }
            }
        });

        put("rm", new Runnable() {
            @Override
            public void run() {
                User user = context.getUser();
                Directory directory = (Directory)user.getDirectory();
                CommandValues values = context.getCommandValues();
                String removeNode = values.getNextParam();

                Node node = search(directory, removeNode);

                if(node == null)
                {
                    context.setErrorMessage("Node could not be found!");
                    return;
                }
                else
                {
                    boolean isLocksWereFound = searchLocks(node);
                    if(isLocksWereFound)
                    {
                        context.setErrorMessage("Node/nodes is/are locked! Please, unlock node/nodes and try again!");
                        return;
                    }
                    boolean result = directory.removeNode(directory, removeNode);
                    context.setCommandWasExecuted(result);
                    context.setBroadcastCommand(true);
                    context.setMessage("Node " + removeNode + " was removed!");
                    if(!result)
                    {
                        context.setErrorMessage("Node could not be removed!");
                    }
                }
            }
        });

        put("unlock", new Runnable() {
            @Override
            public void run() {
                User user = context.getUser();
                Directory directory = (Directory)user.getDirectory();
                CommandValues values = context.getCommandValues();
                String unlockDirectory = values.getNextParam();

                Node node = search(directory, unlockDirectory);
                if(node != null)
                {
                    if(!node.isLock())
                    {
                        context.setErrorMessage("Node is already unlocked!");
                        return;
                    }
                    node.setLock(user, false);
                    if(!node.isLock())
                    {
                        context.setCommandWasExecuted(true);
                        context.setBroadcastCommand(true);
                        context.setMessage("Node " + node.getFullPath() + " was unlocked!");
                    }
                    else
                    {
                        context.setErrorMessage("Node is locked by different user!");
                    }
                }
                else
                {
                    context.setErrorMessage("Node not found!");
                }
            }
        });

    }};

    CommandValues commandValues;
    UserSession userSession;
    NodeService nodeService;

    public CommandLine(UserSession userSession, NodeService nodeService)
    {
        this.userSession = userSession;
        this.nodeService = nodeService;
    }

    public synchronized Context execute(String login, String args)
    {
        args = nodeService.removeDoubleSeparators(args.toLowerCase().trim());

        UserCell userCell = userSession.getUserCell(login);

        Context context = new Context();
        context.setUser(userCell.getUser());
        context.setCommand(args);

        commandValues = context.getCommandValues();
        String command = commandValues.getCommand();

        if (commands.containsKey(command)) {
            commands.get(command).run();
        } else {
            context.setErrorMessage("Such command doesn't exist! Please use help command for getting full list of available commands!");
        }
        return context;
    }

}
