package org.vfs.client;

import org.vfs.client.network.Client;

/**
 * Entry point
 * @author Lipatov Nikita
 */
public class ClientMain
{

    /**
     * @param args no arguments
     */
    public static void main(String[] args)
    {
        Client client = new Client();
        client.run();
    }
}
