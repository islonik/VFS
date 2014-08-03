package org.vfs.server.services;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.utils.NodePrinter;

/**
 * @author Lipatov Nikita
 */
public class NodeServiceTest {

    private LockService lockService = new LockService();
    private NodeService nodeService = new NodeService("/", lockService);
    private NodePrinter nodePrinter = new NodePrinter(lockService);

    @Test
    public void testCreateNode() throws Exception {
        nodeService.createNode(nodeService.getRoot(), "test", NodeTypes.DIR);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|__test\n",
                tree);
    }

    @Test
    public void testCreateNodeStartSlash() throws Exception {
        nodeService.createNode(nodeService.getRoot(), "/test", NodeTypes.DIR);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|__test\n",
                tree);
    }

    @Test
    public void testCreateNodeThreeDirs() throws Exception {
        nodeService.createNode(nodeService.getRoot(), "/test1/test2/test3", NodeTypes.DIR);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|__test1\n" +
                        "|  |__test2\n" +
                        "|  |  |__test3\n",
                tree);
    }

    @Test
    public void testCreateNodeTwoDirsAndFile() throws Exception {
        nodeService.createNode(nodeService.getRoot(), "/test1/test2/weblogic.log", NodeTypes.FILE);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|__test1\n" +
                        "|  |__test2\n" +
                        "|  |  |__weblogic.log\n",
                tree);
    }

    @Test
    public void testCreateNodeFoldersAlreadyExist() throws Exception {
        Node root = new Node("root", NodeTypes.DIR);
        Node test = new Node("logs", NodeTypes.DIR);
        Node file = new Node("weblogic.log", NodeTypes.FILE);
        file.setParent(test);
        test.setParent(root);
        root.setParent(nodeService.getRoot());

        nodeService.createNode(nodeService.getRoot(), "/root/logs/weblogic_clust1.log", NodeTypes.FILE);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assert.assertEquals(
                "/\n" +
                        "|__home\n" +
                        "|__root\n" +
                        "|  |__logs\n" +
                        "|  |  |__weblogic.log\n" +
                        "|  |  |__weblogic_clust1.log\n",
                tree);
    }

    @Test
    public void testClone() throws Exception {
        Node root = new Node("/", NodeTypes.DIR);
        Node home = new Node("home", NodeTypes.DIR);
        home.setParent(root);
        Node boy = new Node("boy", NodeTypes.DIR);
        boy.setParent(home);
        Node girl = new Node("girl", NodeTypes.DIR);
        girl.setParent(home);

        Node homeClone = nodeService.clone(home);

        Assert.assertEquals(home.getChildren().size(), homeClone.getChildren().size());
        Assert.assertNotEquals(home.toString(), homeClone.toString());
        Assert.assertNotEquals(home.getChildren().toArray()[0].toString(), homeClone.getChildren().toArray()[0].toString());
        Assert.assertEquals(((Node) home.getChildren().toArray()[0]).getName(), ((Node) homeClone.getChildren().toArray()[0]).getName());
        Assert.assertNotEquals(home.getChildren().toArray()[1].toString(), homeClone.getChildren().toArray()[1].toString());
        Assert.assertEquals(((Node) home.getChildren().toArray()[1]).getName(), ((Node) homeClone.getChildren().toArray()[1]).getName());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetParentUniqueNames() throws Exception {
        Node root = new Node("/", NodeTypes.DIR);
        Node home1 = new Node("home", NodeTypes.DIR);
        nodeService.setParent(home1, root);
        home1.setParent(root);
        Node home2 = new Node("home", NodeTypes.DIR);
        nodeService.setParent(home2, root); // exception from here
    }
}
