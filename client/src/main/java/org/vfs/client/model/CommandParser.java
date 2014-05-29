package org.vfs.client.model;

import org.vfs.core.network.protocol.RequestFactory;
import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.ResponseFactory;

/**
 * Class of parse command.
 * @author Lipatov Nikita
 */
public class CommandParser
{

    public static final String YOU_ALREADY_AUTHORIZED = "You are already authorized!";
    public static final String YOU_NOT_AUTHORIZED     = "You are not authorized!";
    public static final String TYPE_QUIT_COMMAND      = "For a start you should type 'quit' command!";
    public static final String WRONG                  = "Something wrong!";
    public static final String CONNECT_SERVER         = "Please connect to the server.";

    private Authorization authorization;
    private String outMessage = "";

    public CommandParser(Authorization authorization)
    {
        this.authorization = authorization;
    }

    public String getOutMessage()
    {
        String localOutMessage = outMessage;
        outMessage = "";
        return localOutMessage;
    }

    public boolean isEmptyMessage()
    {
        return outMessage.trim().isEmpty();
    }

    /**
     * The method returns xml as a string.
     * @param fullCommand Command.
     * @return XML as a string.
     */
    public String getXML(String fullCommand)
    {

        String command = getCommand(fullCommand);

        if(command != null)
        {
            User user = this.authorization.getUser();

            if(user == null)
            {
                return null;
            }

            RequestFactory factory = new RequestFactory();
            return factory.create(user.getId(), user.getLogin(), fullCommand).toXml();
        }
        return null;
    }

    public boolean isConnectCommand(String command)
    {
        command = getCommand(command);

        if(command != null)
        {
            return command.equals("connect");
        }
        return false;
    }

    public boolean isQuitCommand(String command)
    {
        command = getCommand(command);

        if(command != null)
        {
            return command.equals("quit");
        }
        return false;
    }

    public boolean isExitCommand(String command)
    {
        command = getCommand(command);
        if(command != null)
        {
            return command.equals("exit");
        }
        return false;
    }

    public String getCommand(String command)
    {
        command = command.toLowerCase().trim();
        return (command.indexOf(" ") != -1)
                ? command.substring(0, command.indexOf(" "))
                : command.substring(0, command.length());
    }

    public String getHost(String fullCommand)
    {
        fullCommand = fullCommand.trim();

        String host = "localhost"; // default value
        if(fullCommand.contains(" "))
        {
            String []args = fullCommand.split(" ");

            if(args.length >= 2)
            {
                host = args[1];
            }
            if(host.contains(":"))
            {
                host = host.substring(0, host.indexOf(":"));
            }
        }
        return host;
    }

    public String getPort(String fullCommand)
    {
        fullCommand = fullCommand.toLowerCase().trim();

        String port = "4499"; // default value
        if(fullCommand.contains(" "))
        {
            String []args = fullCommand.split(" ");

            String host = "localhost";
            if(args.length >= 2)
            {
                host = args[1];
            }

            if(host.contains(":"))
            {
                port = host.substring(host.indexOf(":") + 1, host.length());
            }
        }
        return port;
    }

    public String getLogin(String fullCommand)
    {
        fullCommand = fullCommand.trim();

        String login = "";
        if(fullCommand.contains(" "))
        {
            String []args = fullCommand.split(" ");

            if(args.length >= 3)
            {
                login = args[2];
            }
        }
        if(login.isEmpty())
        {
            throw new RuntimeException("Wrong format!");
        }
        return login;
    }

    public boolean parseServerResponse(String strResponse)
    {
        if(strResponse == null)
        {
            return false;
        }

        ResponseFactory factory = new ResponseFactory();
        Response response = factory.parse(strResponse);

        int code       = Integer.parseInt(response.getCode());
        String message = response.getMessage();

        if(Response.STATUS_SUCCESS_CONNECT == code && !this.authorization.isAuthorized())   // success authorization
        {
            this.authorization.getUser().setId(response.getSpecificCode());
        }
        else if(Response.STATUS_FAIL_CONNECT == code && !this.authorization.isAuthorized()) // fail authorization
        {
            this.authorization.setUser(null);
            if(authorization.getConnection() != null)
            {
                authorization.getConnection().kill();
            }
        }
        else if(Response.STATUS_SUCCESS_QUIT == code && this.authorization.isAuthorized())  // quit response
        {
            this.authorization.setUser(null);
            if(authorization.getConnection() != null)
            {
                authorization.getConnection().kill();
            }
            System.out.println(message);
            return false; // close current connection
        }

        System.out.println(message);
        return true;
    }

    public boolean parseClientCommand(String inputCommand)
    {

        // connect command
        if(this.isConnectCommand(inputCommand))
        {
            if(this.authorization.isAuthorized())
            {
                outMessage = YOU_ALREADY_AUTHORIZED;
                return true;
            }

            User user = new User("0", this.getLogin(inputCommand));
            this.authorization.setUser(user);

            if(!this.authorization.sendConnectCommand(this.getHost(inputCommand), this.getPort(inputCommand)))
            {
                System.out.println(this.authorization.getErrorMessage());
            }
            return true;
        }
        // quit command
        else if(this.isQuitCommand(inputCommand))
        {
            if(!this.authorization.isAuthorized())
            {
                outMessage = YOU_NOT_AUTHORIZED;
                return true;
            }
        }
        else if(this.isExitCommand(inputCommand))
        {
            if(this.authorization.isAuthorized())
            {
                outMessage = TYPE_QUIT_COMMAND;
                return true;
            }
            else
            {
                return false;
            }
        }

        if(this.authorization.isAuthorized())
        {
            // other commands
            String xml = this.getXML(inputCommand);

            if(xml == null)
            {
                outMessage = WRONG;
                return true;
            }
            this.authorization.getConnection().flush(xml);
        }
        else
        {
            outMessage = CONNECT_SERVER;
        }
        return true;
    }

}


