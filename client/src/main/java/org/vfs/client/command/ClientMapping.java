package org.vfs.client.command;

import org.vfs.core.command.AbstractCommandMapping;
import org.vfs.core.command.CommandMapping;

/**
 * @author Nikita Lipatov
 */
public class ClientMapping extends AbstractCommandMapping implements CommandMapping
{
    public ClientMapping()
    {
        super(ClientMapping.class);
    }
}
