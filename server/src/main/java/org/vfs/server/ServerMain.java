package org.vfs.server;

import org.vfs.server.network.Server;

/**
 * Entry point
 * @author Lipatov Nikita
 */
public class ServerMain
{

    /**
     * @param args no arguments
     */
    public static void main(String[] args)
    {
        Server server = new Server();
        server.run();
    }
}
