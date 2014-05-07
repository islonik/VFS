package org.vfs.server.network;

import java.net.*;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server class.
 * @author Lipatov Nikita
 */
public class Server
{

    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static Server instance = null;

    private static String server ;
    private static int port = -1;
    private static int connectionPool = -1;

    private Server()
    {
        try
        {
            ServerSettings settings = ServerSettings.getInstance();
            server         = settings.getServerName();
            port           = Integer.parseInt(settings.getServerPort());
            connectionPool = Integer.parseInt(settings.getConnectionPool());

            validateServerParameters();

            InetAddress address = InetAddress.getByName(server);
            ServerSocket server = new ServerSocket(port, connectionPool, address);

            String serverHello = "\n" +
                "Server host address - " + server.getInetAddress().getHostAddress() + "\n" +
                "Server host name - " + server.getInetAddress().getHostName() + "\n" +
                "Server port - " + server.getLocalPort() + "\n";

            log.info(serverHello);
            System.out.println(serverHello);

            while (true)
            {
                Socket client = server.accept();
                if (client != null)
                {
                    log.info
                    (
                        "New client from " + client.getInetAddress()
                                + ":" + Integer.toString(client.getPort()) + " connected"
                    );

                    ServerThread handler = new ServerThread(client);
                    handler.start();
                }
            }
        }
        catch (IOException ioe)
        {
            log.error(ioe.getLocalizedMessage(), ioe);
        }
    }

    public void validateServerParameters() throws IOException
    {
        if(port == -1)
        {
            throw new IOException("Server port is -1");
        }
        if(connectionPool == -1)
        {
            throw new IOException("Server connection pool is -1");
        }
    }

    public static Server run()
    {
        Server localInstance = instance;
        if(localInstance == null)
        {
            synchronized (Server.class)
            {
                localInstance = instance;
                if(localInstance == null)
                {
                    instance = localInstance = new Server();
                }
            }
        }
        return localInstance;
    }
}

