package org.vfs.server.command;

import org.vfs.core.command.AbstractCommandMapping;
import org.vfs.core.command.CommandMapping;

/**
 * @author Lipatov Nikita
 */
public class ServerMapping extends AbstractCommandMapping implements CommandMapping
{

    public ServerMapping()
    {
        super(ServerMapping.class);
    }

}
