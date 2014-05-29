package org.vfs.core.network.protocol;

/**
 * @author Lipatov Nikita
 */
public interface Request
{
    public String getUserId();
    public String getUserLogin();

    public void setCommand(String command);
    public String getCommand();

    public String toXml();
}
