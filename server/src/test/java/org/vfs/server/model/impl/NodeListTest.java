package org.vfs.server.model.impl;

import org.junit.Test;
import org.junit.Assert;

import java.util.List;

/**
 * User: Lipatov Nikita
 */
public class NodeListTest
{
	
	@Test
	public void testNodeList_testCase01()
    {
		
		NodeList nodeList = new NodeList();

        Directory dir1 = new Directory("dir_1");
        Directory dir2 = new Directory("dir_2");
        Directory dir3 = new Directory("dir_3");

        nodeList.add(dir1);
		nodeList.add(dir2);
		nodeList.add(dir3);

		nodeList.add(new File("file_1.jpg"));
		nodeList.add(new File("file_3.jpg"));
		nodeList.add(new File("file_2.jpg"));
		nodeList.add(new File("file_4.jpg"));
		nodeList.add(new File("file_7.jpg"));
		nodeList.add(new File("file_10.jpg"));
		nodeList.add(new File("file_14.jpg"));
		nodeList.add(new File("file_24.jpg"));
		
		Assert.assertEquals(3, nodeList.getDirectories().size());
		Assert.assertEquals(8, nodeList.getFiles().size());
		
		Assert.assertEquals("file_10.jpg", (nodeList.search(new File("file_10.jpg"))).getName());
		
		List<Directory> directories = nodeList.getDirectories();
        Assert.assertTrue(directories.contains(dir1));
        Assert.assertTrue(directories.contains(dir2));
        Assert.assertTrue(directories.contains(dir3));
	}

    @Test
    public void testNodeList_testCase02()
    {

        NodeList nodeList = new NodeList();

        Directory dir1 = new Directory("dir_1");
        Directory dir2 = new Directory("dir_2");
        Directory dir3 = new Directory("dir_3");

        Assert.assertTrue(nodeList.add(dir1));
        Assert.assertTrue(nodeList.add(dir2));
        Assert.assertTrue(nodeList.add(dir3));

        File file1 = new File("file_1.jpg");
        File file2 = new File("file_2.jpg");
        File file3 = new File("file_3.jpg");

        Assert.assertTrue(nodeList.add(file1));
        Assert.assertTrue(nodeList.add(file2));
        Assert.assertTrue(nodeList.add(file3));
        Assert.assertFalse(nodeList.add(file3));

        Assert.assertEquals(3, nodeList.getDirectories().size());
        Assert.assertEquals(3, nodeList.getFiles().size());

        Assert.assertTrue(nodeList.remove(dir1));
        Assert.assertTrue(nodeList.remove(dir3));
        Assert.assertTrue(nodeList.remove(file2));
        Assert.assertTrue(nodeList.remove(file3));

        Assert.assertFalse(nodeList.remove(dir3));
        Assert.assertFalse(nodeList.remove(file3));

        Assert.assertEquals("dir_2",      (nodeList.search(dir2)).getName());
        Assert.assertEquals("file_1.jpg", (nodeList.search(file1)).getName());
        Assert.assertNull((nodeList.search(file2)));

    }
}
