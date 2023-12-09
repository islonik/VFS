package org.vfs.server.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.vfs.server.Application;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.utils.NodePrinter;

/**
 * @author Lipatov Nikita
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = "test")
@TestMethodOrder(MethodOrderer.MethodName.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NodeServiceTest {

    @Autowired
    private NodeManager nodeManager;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodePrinter nodePrinter;

    @BeforeEach
    public void setUp() {
        nodeService.initDirs();
    }

    @Test
    public void testClone() {
        Node home = this.nodeService.getHome();
        Node boy  = this.nodeManager.newNode("boy",  NodeTypes.DIR);
        Node girl = this.nodeManager.newNode("girl", NodeTypes.DIR);
        this.nodeManager.setParent(boy, home);
        this.nodeManager.setParent(girl, home);
        Node homeClone = nodeService.clone(home);

        Assertions.assertEquals(home.getChildren().size(), homeClone.getChildren().size());
        Assertions.assertNotEquals(home.toString(), homeClone.toString());
        Assertions.assertNotEquals(home.getChildren().toArray()[0].toString(), homeClone.getChildren().toArray()[0].toString());
        Assertions.assertEquals(((Node) home.getChildren().toArray()[0]).getName(), ((Node) homeClone.getChildren().toArray()[0]).getName());
        Assertions.assertNotEquals(home.getChildren().toArray()[1].toString(), homeClone.getChildren().toArray()[1].toString());
        Assertions.assertEquals(((Node) home.getChildren().toArray()[1]).getName(), ((Node) homeClone.getChildren().toArray()[1]).getName());
    }

    @Test
    public void testCreateNode() {
        nodeService.createNode(nodeService.getRoot(), "test", NodeTypes.DIR);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assertions.assertEquals(
                """
                /
                |__home
                |__test
                """,
                tree);
    }

    @Test
    public void testCreateNodeStartSlash() {
        nodeService.createNode(nodeService.getRoot(), "/test", NodeTypes.DIR);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assertions.assertEquals(
                """
                /
                |__home
                |__test
                """,
                tree
        );
    }

    @Test
    public void testCreateNodeThreeDirs() {
        nodeService.createNode(nodeService.getRoot(), "/test1/test2/test3", NodeTypes.DIR);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assertions.assertEquals(
                """
                /
                |__home
                |__test1
                |  |__test2
                |  |  |__test3
                """,
                tree
        );
    }

    @Test
    public void testCreateNodeTwoDirsAndFile() {
        nodeService.createNode(nodeService.getRoot(), "/test1/test2/weblogic.log", NodeTypes.FILE);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assertions.assertEquals(
                """
                /
                |__home
                |__test1
                |  |__test2
                |  |  |__weblogic.log
                """,
                tree
        );
    }

    @Test
    public void testCreateNodeFoldersAlreadyExist() {
        Node root = new Node("root", NodeTypes.DIR);
        Node test = new Node("logs", NodeTypes.DIR);
        Node file = new Node("weblogic.log", NodeTypes.FILE);
        file.setParent(test);
        test.setParent(root);
        root.setParent(nodeService.getRoot());

        nodeService.createNode(nodeService.getRoot(), "/root/logs/weblogic_clust1.log", NodeTypes.FILE);
        String tree = nodePrinter.print(nodeService.getRoot());
        Assertions.assertEquals(
                """
                /
                |__home
                |__root
                |  |__logs
                |  |  |__weblogic.log
                |  |  |__weblogic_clust1.log
                """,
                tree
        );
    }

    @Test
    public void testSetParentUniqueNames() {
        Node root = new Node("/", NodeTypes.DIR);
        Node home1 = new Node("home", NodeTypes.DIR);
        nodeService.getNodeManager().setParent(home1, root);
        home1.setParent(root);
        Node home2 = new Node("home", NodeTypes.DIR);

        IllegalArgumentException thrown = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> nodeService.getNodeManager().setParent(home2, root)
        );

        Assertions.assertEquals("Name already exist for /", thrown.getMessage());
    }
}
