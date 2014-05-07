package org.vfs.server.network;

import java.util.ResourceBundle;

/**
 * Class of server settings.
 * @author Lipatov Nikita
 */
public class ServerSettings
{

    private static ServerSettings instance = null;
    private String serverName = null;
    private String serverPort = null;
    private String connectionPool = null;
    private String timeout = null;

    /**
     * The constructor gets the settings from property file.
     */
    private ServerSettings()
    {
        String bundleName = "settings";
        ResourceBundle settings = ResourceBundle.getBundle(bundleName);
        serverName     = settings.getString("serverName");
        serverPort     = settings.getString("serverPort");
        connectionPool = settings.getString("connectionPool");
        timeout        = settings.getString("timeout");
    }

    public static ServerSettings getInstance()
    {
        ServerSettings localInstance = instance;
        if(localInstance == null)
        {
            synchronized (ServerSettings.class)
            {
                localInstance = instance;
                if(localInstance == null)
                {
                    instance = localInstance = new ServerSettings();
                }
            }
        }
        return localInstance;
    }

    public String getServerName()
    {
        if(serverName == null)
        {
            serverName = "localhost"; // default value
        }
        return serverName;
    }

    public String getServerPort()
    {
        if(serverPort == null)
        {
            serverPort = "4999"; // default value
        }
        return serverPort;
    }

    public String getConnectionPool()
    {
        if(connectionPool == null)
        {
            connectionPool = "100"; // default value
        }
        return connectionPool;
    }

    public String getTimeout()
    {
        if(timeout == null)
        {
            timeout = "10"; // default value
        }
        return timeout;
    }

}
