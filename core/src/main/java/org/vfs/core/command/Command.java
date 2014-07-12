package org.vfs.core.command;

import org.vfs.core.model.Context;

/**
 * @author Lipatov Nikita
 */
public interface Command
{
    public String getCommandName();
    public boolean isBroadcastCommand();
    public void action(Context context);

}
