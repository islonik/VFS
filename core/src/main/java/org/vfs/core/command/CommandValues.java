package org.vfs.core.command;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lipatov Nikita
 */
public class CommandValues
{
    private String source;
    private String command;
    private List<String> keys   = new ArrayList<String>();
    private List<String> params = new ArrayList<String>();
    private int keyPointer = 0;
    private int paramPointer = 0;

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public List<String> getParams()
    {
        return params;
    }

    public void setParams(List<String> params)
    {
        this.params = params;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public List<String> getKeys()
    {
        return keys;
    }

    public void setKeys(List<String> keys)
    {
        this.keys = keys;
    }

    public String getNextKey()
    {
        if(keys.size() > keyPointer)
        {
            String key = keys.get(keyPointer++);

            return key;
        }
        return null;
    }

    public String getNextParam()
    {
        if(params.size() > paramPointer)
        {
            String param = params.get(paramPointer++);

            return param;
        }
        return null;
    }


}
