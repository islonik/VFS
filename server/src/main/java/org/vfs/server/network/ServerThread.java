package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.Request;
import org.vfs.core.network.protocol.RequestFactory;
import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.ResponseFactory;
import org.vfs.server.command.CommandLine;
import org.vfs.server.model.Context;
import org.vfs.server.user.User;
import org.vfs.server.user.UserRegistry;
import org.vfs.server.user.UserSecurity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Class of threads.
 * @author Lipatov Nikita
 */
class ServerThread extends Thread
{
    private static final Logger log = LoggerFactory.getLogger(ServerThread.class);

    // id - ip:port
    private static Hashtable<String, ServerThread> threads = new Hashtable();
    private Socket clientSocket = null;
    private DataInputStream inStream = null;
    private DataOutputStream outStream = null;

    private String id = "";                       // ID of ServerThread.
    private ThreadCollector threadKiller = null;  // Object for timeout.
    private User user;
    private String userId;
    private UserSecurity security;

    public ServerThread(Socket socket)
    {
        try
        {
            this.clientSocket = socket;
            this.inStream     = new DataInputStream(socket.getInputStream());
            this.outStream    = new DataOutputStream(socket.getOutputStream());
            this.id           = socket.getInetAddress().toString() + ":" + Integer.toString(socket.getPort());
            this.threadKiller = new ThreadCollector(this);
            this.security = new UserSecurity();
        }
        catch (IOException ioe)
        {
            log.error(ioe.getLocalizedMessage(), ioe);
        }
    }

    public String getID()
    {
        return id;
    }

    public static Hashtable getThreads()
    {
        return threads;
    }

    @Override
    public void run()
    {
        try
        {
            threads.put(id, this);
            // all incoming requests
            while (true)
            {
                DataInputStream in = inStream;

                String strRequest = in.readUTF();

                threadKiller.resetTimer();
                if (!parse(strRequest))
                {
                    break;
                }
            }
        }
        catch (SocketException se)
        {
            if(id != null)
            {
                log.error("SocketException with id = " + id + " because " + se.getLocalizedMessage(), se);
            }
            else
            {
                log.error(se.getLocalizedMessage(), se);
            }
        }
        catch (IOException ioe)
        {
            log.error("run method: " + ioe.getLocalizedMessage(), ioe);
        }
        finally
        {
            kill();
        }
    }

    private boolean parse(String requestMessage)
    {
        // get request and validate security
        RequestFactory requestFactory = new RequestFactory();
        Request request = requestFactory.parse(requestMessage);

        // execute command
        userId = request.getUserId();
        user = UserRegistry.getInstance().getUser(request.getUserLogin());

        String command = request.getCommand();
        CommandLine cmd = new CommandLine();
        Context context = cmd.toContext(user, command);
        cmd.execute(context);

        // send feedback to user
        ResponseFactory responseFactory = new ResponseFactory();
        Response response = responseFactory.create(context.getCode(), context.getSpecificCode(), context.getMessage());
        pointToPoint(response.toXml());

        // send feedback to all
        if(context.isBroadcastCommand() && context.isCommandWasExecuted())
        {
            broadcast(context.getUser(), request);
        }

        // stop thread
        if(context.isThreadClose())
        {
            return false;
        }
        return true;
    }

    /**
     * feedback to thread user
     * @param message response.
     */
    public void pointToPoint(String message)
    {
        ServerThread thread = threads.get(id);
        try
        {
            synchronized (thread.outStream)
            {
                thread.outStream.writeUTF(message + "\n");
                thread.outStream.flush();
            }
        }
        catch (Exception error)
        {
            kill();
        }
    }

    /**
     * Send notification about actions to other users
     */
    public void broadcast(User user, Request request)
    {
        String message = user.getLogin() + " performs command: " + request.getCommand() + "\n";
        ResponseFactory factory = new ResponseFactory();
        Response response = factory.create(Response.STATUS_OK, -1, message);

        synchronized (threads)
        {
            Set<Map.Entry<String, ServerThread>> threads = ServerThread.threads.entrySet();
            for (Map.Entry<String, ServerThread> threadMap : threads)
            {
                String id = threadMap.getValue().getID();
                if (!this.id.equals(id))
                {
                    try
                    {
                        synchronized (threadMap.getValue().outStream)
                        {
                            threadMap.getValue().outStream.writeUTF(response.toXml());
                            threadMap.getValue().outStream.flush();
                        }
                    }
                    catch (Exception error)
                    {
                        kill();
                    }
                }
            }
        }
    }

    public void kill()
    {
        try
        {
            if(clientSocket != null)
            {
                if (!clientSocket.isClosed())
                {
                    clientSocket.close();
                }
            }
            if(id != null)
            {
                threads.remove(id);
                if(user != null)
                {
                    UserRegistry.getInstance().removeUser(userId, user.getLogin());
                }
                log.info("The client with id = " + id + " was disconnected");
                id = null;
            }
            if(threadKiller != null)
            {
                threadKiller.stop();
                threadKiller = null;
            }
            this.interrupt();
        }
        catch (IOException ioe)
        {
            log.error("close socket: " + ioe);
        }
    }


}
