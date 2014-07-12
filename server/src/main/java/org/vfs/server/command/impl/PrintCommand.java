package org.vfs.server.command.impl;

import org.vfs.core.network.protocol.User;
import org.vfs.core.command.Command;
import org.vfs.core.model.Context;
import org.vfs.server.model.Node;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;

import java.util.List;

/**
 * @author Lipatov Nikita
 */
public class PrintCommand extends AbstractServerCommand implements Command
{

    public PrintCommand()
    {
        this.commandName = "print";
    }

    public void action(Context context)
    {
        User user = context.getUser();
        Directory directory = (Directory)user.getDirectory();

        String tree = printTree(directory);
        context.setCommandWasExecuted(true);
        context.setMessage(tree);
    }

    public String printTree(Directory directory)
    {
        return simplePrintTreeDir(directory, new StringBuilder(), 0).toString();
    }

    /**
     * The method prints tree on the screen.
     * @param parentDir Directory.
     * @param textTree String with nodes.
     * @param deep Depth from root.
     * @return
     */
    private StringBuilder simplePrintTreeDir(Directory parentDir, StringBuilder textTree, int deep)
    {
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

    /**
     * The method prints the files and their status.
     * @param node File.
     * @param textTree StringTree.
     * @param deep Depth from root.
     */
    private void simplePrintTreeNode(Node node, StringBuilder textTree, int deep)
    {
        if (deep != 0)
        {
            simplePrintHorizontalLine(textTree, deep);
        }
        textTree.append(node.getName());
        if (node.isLock())
        {
            textTree.append(" [Locked by ");
            textTree.append(node.getLockByUser().getLogin());
            textTree.append(" ]");
        }
        textTree.append("\n");
    }

    /**
     * The method prints horizontal string.
     * @param tree StringTree.
     * @param deep Depth from root.
     */
    private void simplePrintHorizontalLine(StringBuilder tree, int deep)
    {
        for (int i = 0; i < deep; i++)
        {
            tree.append("|");
            if (i + 1 != deep)
            {
                tree.append("  ");
            }
        }
        tree.append("__");
    }

}
