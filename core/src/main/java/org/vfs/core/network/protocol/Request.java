package org.vfs.core.network.protocol;

/**
 * @author Lipatov Nikita
 */
public interface Request
{
    public String getUserId();
    public String getUserLogin();
    public String getCommand();

    public void parse(String data);

    public String toXml();
}
