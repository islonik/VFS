package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.core.network.protocol.*;
import org.vfs.server.CommandLine;
import org.vfs.core.model.Context;
import org.vfs.server.user.UserSession;
import org.vfs.server.user.UserService;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Class of threads.
 * @author Lipatov Nikita
 */
public class ServerThread extends Thread
{
    private static final Logger log = LoggerFactory.getLogger(ServerThread.class);

    // id - ip:port
    private static Hashtable<String, ServerThread> threads = new Hashtable();
    private Socket clientSocket = null;
    private BufferedWriter outStream = null;
    private BufferedReader inStream = null;

    private String id = "";                       // ID of ServerThread.
    //private ThreadCollector threadKiller = null;  // Object for timeout.
    private User user;
    private String userId;
    private UserService service;

    public ServerThread(Socket socket)
    {
        try
        {
            this.clientSocket = socket;
            this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.id           = socket.getInetAddress().toString() + ":" + Integer.toString(socket.getPort());
            //this.threadKiller = new ThreadCollector(this);
            this.service      = new UserService();
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
                BufferedReader in = inStream;

                StringBuilder stringBuilder = new StringBuilder();
                while(in.ready())
                {
                    stringBuilder.append(in.readLine());
                }
                if(stringBuilder.length() == 0)
                {
                    continue;
                }

                String strRequest = stringBuilder.toString();

                //threadKiller.resetTimer();
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
        userId = request.getUser().getId();
        user = UserSession.getInstance().getUser(request.getUser().getLogin());

        String command = request.getCommand();
        CommandLine cmd = new CommandLine();
        Context context = cmd.execute(user, command);

        // send feedback to user
        ResponseService responseService = new ResponseService();
        Response response = responseService.create(context.getCode(), context.getSpecificCode(), context.getMessage());
        pointToPoint(responseService.toXml(response));

        // send feedback to all
        if(context.isBroadcastCommand() && context.isCommandWasExecuted())
        {
            broadcast(context.getUser(), request);
        }

        // stop thread
        if(context.isExit())
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
                thread.outStream.write(message, 0, message.length());
                thread.outStream.newLine();
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
        ResponseService responseService = new ResponseService();
        Response response = responseService.create(Response.STATUS_OK, -1, message);

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
                            String xmlResponse = responseService.toXml(response);
                            threadMap.getValue().outStream.write(xmlResponse, 0, xmlResponse.length());
                            threadMap.getValue().outStream.newLine();
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
                    UserSession.getInstance().removeUser(userId, user.getLogin());
                }
                log.info("The client with id = " + id + " was disconnected");
                id = null;
            }
            /*if(threadKiller != null)
            {
                threadKiller.stop();
                threadKiller = null;
            }
            this.interrupt();*/
        }
        catch (IOException ioe)
        {
            log.error("close socket: " + ioe);
        }
    }


}
