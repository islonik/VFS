package org.vfs.server.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.services.LockService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Lipatov Nikita
 */
@Component
public class NodePrinter {

    private final LockService lockService;

    @Autowired
    public NodePrinter(LockService lockService) {
        this.lockService = lockService;
    }

    public String print(Node directory) {
        if (directory.getType() != NodeTypes.DIR) {
            throw new IllegalArgumentException("Node directory is FILE!");
        }
        return printTreeDir(directory, new StringBuilder(), 0).toString();
    }

    /**
     * The method prints tree on the screen.
     *
     * @param parentDir Directory.
     * @param textTree  String with nodes.
     * @param deep      Depth from root.
     * @return
     */
    private StringBuilder printTreeDir(Node parentDir, StringBuilder textTree, int deep) {
        printTreeNode(parentDir, textTree, deep);

        deep++;

        Collection<Node> children = parentDir.getChildren();
        List<Node> directories = new ArrayList<>();
        List<Node> files = new ArrayList<>();
        for (Node child : children) {
            if (child.getType() == NodeTypes.DIR) {
                directories.add(child);
            } else {
                files.add(child);
            }
        }
        Collections.sort(directories);
        Collections.sort(files);

        for (Node directory : directories) {
            printTreeDir(directory, textTree, deep);
        }

        for (Node file : files) {
            printTreeNode(file, textTree, deep);
        }

        return textTree;
    }

    /**
     * The method prints the files and their status.
     *
     * @param node     File.
     * @param textTree StringTree.
     * @param deep     Depth from root.
     */
    private void printTreeNode(Node node, StringBuilder textTree, int deep) {
        if (deep != 0) {
            simplePrintHorizontalLine(textTree, deep);
        }
        textTree.append(node.getName());
        if (lockService.isLocked(node) && lockService.getUser(node) != null) {
            textTree.append(" [Locked by ");
            textTree.append(lockService.getUser(node).getLogin());
            textTree.append(" ]");
        }
        textTree.append("\n");
    }

    /**
     * The method prints horizontal string.
     *
     * @param tree StringTree.
     * @param deep Depth from root.
     */
    private void simplePrintHorizontalLine(StringBuilder tree, int deep) {
        for (int i = 0; i < deep; i++) {
            tree.append("|");
            if (i + 1 != deep) {
                tree.append("  ");
            }
        }
        tree.append("__");
    }
}
