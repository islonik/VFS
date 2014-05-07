package org.vfs.server.command;

import org.vfs.server.model.Context;

/**
 * @author Lipatov Nikita
 */
public interface Command
{

    public String getCommandName();
    public boolean isBroadcastCommand();
    public Context parse(String command, String args);
    public void action(Context context);

}
