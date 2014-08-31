package org.vfs.server.commands;

import org.vfs.core.command.CommandValues;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;

/**
 * @author Lipatov Nikita
 */
public interface Command {

    public void apply(UserSession userSession, CommandValues values, ClientWriter clientWriter);
}
