package org.vfs.core.command;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lipatov Nikita
 */
public class CommandValues {
    private String source;
    private String command;
    private List<String> keys   = new ArrayList<>();
    private List<String> params = new ArrayList<>();
    private int keyPointer = 0;
    private int paramPointer = 0;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getNextKey() {
        return (keys.size() > keyPointer)
                ? keys.get(keyPointer++)
                : null;
    }

    public String getNextParam() {
        return (params.size() > paramPointer)
                ? params.get(paramPointer++)
                : null;
    }


}
