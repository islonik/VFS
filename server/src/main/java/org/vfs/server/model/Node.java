package org.vfs.server.model;

import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.User;

/**
 * @author Lipatov
 * @date 11.02.14
 */
public interface Node {

    public enum NodeType
    {
        DIR,
        FILE
    }

	public void setParent(Directory parent);
	public Directory getParent();
	public void setName(String name);
	public String getName();
	public NodeType getType();
    public String getFullPath();

    public boolean isLock();
    public boolean isLock(User user);
    public void setLock(User user, boolean isLock);
    public User getLockByUser();
    public Node copy();

}
