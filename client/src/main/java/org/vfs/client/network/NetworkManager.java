package org.vfs.client.network;

import org.vfs.client.model.MessageSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author Lipatov Nikita
 */
public class NetworkManager
{
    private static NetworkManager instance;

    private ExecutorService executorService;
    private Socket socket;

    private BlockingQueue<String> toUserQueue;
    private BlockingQueue<String> toServerQueue;

    private IncomingMessageListener incomingMessageListener;
    private MessageSender messageSender;

    private SocketReader socketReader;
    private SocketWriter socketWriter;

    public static NetworkManager getInstance()
    {
        if(instance == null)
        {
            instance = new NetworkManager();
        }
        return instance;
    }

    public ExecutorService getExecutorService()
    {
        return executorService;
    }

    public MessageSender getMessageSender()
    {
        return messageSender;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void closeSocket()
    {
        try
        {
            if(socket != null)
            {
                socket.close();
                System.err.println("Socket was closed!!!!");
            }
        }
        catch(IOException ioe)
        {
            System.err.println("NetworkManager.IOException.Message= " + ioe.getMessage());
        }
    }

    public void createServerConnection(String serverHost, String serverPort) throws IOException
    {
        boolean isNullSocket = (socket == null) ? true : false;

        InetAddress ipAddress = InetAddress.getByName(serverHost);
        this.socket = new Socket(ipAddress, Integer.parseInt(serverPort));

        if(isNullSocket)
        {
            this.executorService = Executors.newFixedThreadPool(4);

            this.toUserQueue = new ArrayBlockingQueue<String>(1024);
            this.toServerQueue = new ArrayBlockingQueue<String>(1024);

            this.socketReader = new SocketReader(socket, toUserQueue);
            this.socketWriter = new SocketWriter(socket, toServerQueue);

            this.incomingMessageListener = new IncomingMessageListener(toUserQueue);
            this.messageSender = new MessageSender(toServerQueue);

            executorService.execute
            (
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        socketReader.run();
                    }
                }
            );

            executorService.execute
            (
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        incomingMessageListener.run();
                    }
                }
            );

            executorService.execute
            (
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        socketWriter.run();
                    }
                }
            );
        }
        else
        {
            this.socketReader.setSocket(this.socket);
            this.socketWriter.setSocket(this.socket);
        }
     }

}
