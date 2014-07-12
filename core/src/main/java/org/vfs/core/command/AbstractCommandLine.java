package org.vfs.core.command;
import org.vfs.core.model.Context;
import org.vfs.core.network.protocol.User;

/**
 * @author Lipatov Nikita
 */
public abstract class AbstractCommandLine
{
    public abstract Context execute(User user, String args);

    public String removeDoubleSlashes(String path)
    {
        if(path.contains("//"))
        {
            path = path.replaceAll("//", "/");
            return removeDoubleSlashes(path);
        }
        return path;
    }

    public String trimSlashes(String path)
    {
        if(path.startsWith("/"))
        {
            path = path.substring(1, path.length());
        }

        if(path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
