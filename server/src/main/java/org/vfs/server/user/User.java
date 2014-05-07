package org.vfs.server.user;

import org.vfs.server.model.NodeFactory;
import org.vfs.server.model.impl.Directory;

/**
 * @author Lipatov Nikita
 */
public class User
{
    private long id;
    private String login;
    private Directory directory;

    public User(String login)
    {
        this.id = Long.parseLong(GeneratorID.getInstance().getId());
        this.login = login;

        Directory homeDirectory = NodeFactory.getFactory().createDirectory("home/" + login);

        this.directory = homeDirectory ;
    }

    public long getId()
    {
        return id;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public Directory getDirectory()
    {
        return directory;
    }

    public void setDirectory(Directory directory)
    {
        this.directory = directory;
    }
}
