package org.vfs.server;

/**
 * Entry point
 * @author Lipatov Nikita
 */
public class Application
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
