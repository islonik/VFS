package org.vfs.server.model;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.model.impl.File;

/**
 * @author Lipatov Nikita
 */
public class TreeStructureTest
{

    TreeStructure tree = new TreeStructure();

    @Test
    public void testTreeStructure_simplePrint_testCase01()
    {
        Directory rootDirectory = new Directory("/");
        Directory dir1 = new Directory("dir1");
        Directory dir2 = new Directory("dir2");
        File file1 = new File("nclog.log");
        File file2 = new File("image.jpg");

        dir1.addNode(file1);
        dir2.addNode(file2);

        rootDirectory.addNode(dir1);
        rootDirectory.addNode(dir2);

        Assert.assertEquals
        (
            "/\n" +
            "|__dir1\n" +
            "|  |__nclog.log\n" +
            "|__dir2\n" +
            "|  |__image.jpg\n",
            tree.printTree(rootDirectory)
        );

    }
}
