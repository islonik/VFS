package org.vfs.core.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reflections.Reflections;

import java.util.*;

/**
 *
 *   Load all classes where interface is Command and create mapping
 *   required dependency:
 *
 *   <dependency>
 *       <groupId>org.reflections</groupId>
 *       <artifactId>reflections</artifactId>
 *       <version>0.9.9-RC1</version>
 *   </dependency>
 *
 * @author Lipatov Nikita
 */
public abstract class AbstractCommandMapping
{
    protected static final Logger log = LoggerFactory.getLogger(AbstractCommandMapping.class);

    protected HashMap<String, Command> mapping;

    /**
     * @param classInstance For example, Command.class
     */
    public AbstractCommandMapping(Class classInstance)
    {
        this(classInstance.getPackage().getName());
    }

    /**
     *  @param packageName For example, org.vfs.server.command
     **/
    public AbstractCommandMapping(String packageName)
    {
        mapping = new HashMap<String, Command>();
        try
        {
            Reflections reflections = new Reflections(packageName);
            Set<Class<? extends Command>> commands = reflections.getSubTypesOf(Command.class);
            Iterator<Class<? extends Command>> iterator = commands.iterator();
            while(iterator.hasNext())
            {
                Class command = iterator.next();

                Command com = (Command)Class.forName(command.getName()).getConstructor().newInstance();
                mapping.put(com.getCommandName(), com);
            }
        }
        catch(Exception cnfe)
        {
            log.error(cnfe.getMessage(), cnfe);
        }
    }

    public HashMap<String, Command> getMapping()
    {
        return mapping;
    }

}
