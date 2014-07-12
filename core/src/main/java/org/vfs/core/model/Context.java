package org.vfs.core.model;

import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.Response;
import org.vfs.core.network.protocol.User;

/**
 * @author Lipatov Nikita
 */
public class Context
{
    private User user;

    private CommandValues commandValues;
    private boolean isCommandWasExecuted = false;
    private boolean isBroadcastCommand = false;
    private boolean isThreadClose = false;

    private String message;
    private String errorMessage;

    private int code = Response.STATUS_OK; // default value from Response
    private long specificCode = -1;        // no user id

    {
        this.message = "";
        this.errorMessage = "";
    }

    public Context()
    {
    }

    public Context(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public CommandValues getCommandValues()
    {
        return commandValues;
    }

    public void setCommandValues(CommandValues commandValues)
    {
        this.commandValues = commandValues;
    }

    public boolean isCommandWasExecuted()
    {
        return isCommandWasExecuted;
    }

    public void setCommandWasExecuted(boolean isCommandWasExecuted)
    {
        this.isCommandWasExecuted = isCommandWasExecuted;
    }

    public boolean isBroadcastCommand()
    {
        return isBroadcastCommand;
    }

    public void setBroadcastCommand(boolean isBroadcastCommand)
    {
        this.isBroadcastCommand = isBroadcastCommand;
    }

    public boolean isThreadClose()
    {
        return isThreadClose;
    }

    public void setThreadClose(boolean isThreadClose)
    {
        this.isThreadClose = isThreadClose;
    }

    public String getMessage()
    {
        return (this.errorMessage.isEmpty()) ? message : errorMessage;
    }

    public void setMessage(String message)
    {
        this.isCommandWasExecuted = true;
        this.message = message;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        if(this.code == Response.STATUS_OK)
        {
            this.code = Response.STATUS_FAIL;
        }
        this.isCommandWasExecuted = false;
        this.errorMessage = errorMessage;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public long getSpecificCode()
    {
        return specificCode;
    }

    public void setSpecificCode(long specificCode)
    {
        this.specificCode = specificCode;
    }


}
