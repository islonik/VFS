package org.vfs.server.model;

import org.vfs.core.network.protocol.Response;
import org.vfs.server.user.User;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public class Context
{
    private User user;
    private HashMap<String, String> keys;
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
        this.keys = new HashMap<String, String>();
    }

    public Context(User user, HashMap<String, String> keys)
    {
        this.user = user;
        this.keys = keys;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public HashMap<String, String> getKeys()
    {
        return keys;
    }

    public void setKeys(HashMap<String, String> keys)
    {
        this.keys = keys;
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

    public String getCommand()
    {
        return getArg("command");
    }

    public String getArg1()
    {
        return getArg("arg1");
    }

    public String getArg2()
    {
        return getArg("arg2");
    }

    public String getArgN(int n)
    {
        return getArg("arg" + n);
    }

    public String getArgByKey(String key)
    {
        return getArg(key);
    }

    private String getArg(String name)
    {
        return this.keys.get(name);
    }
}
