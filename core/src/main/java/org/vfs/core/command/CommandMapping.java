package org.vfs.core.command;

import java.util.HashMap;

/**
 * @author Lipatov Nikita
 */
public interface CommandMapping
{
    public HashMap<String, Command> getMapping();
}
