package org.vfs.server.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reflections.Reflections;

import java.util.*;

/**
 * @author Lipatov Nikita
 */
public class CommandMapping
{
    private static final Logger log = LoggerFactory.getLogger(CommandMapping.class);

    private static volatile CommandMapping instance;
    private HashMap<String, Command> mapping;

    /**
     * load all classes with interface Command and create mapping
     * required dependency:

     <dependency>
         <groupId>org.reflections</groupId>
         <artifactId>reflections</artifactId>
         <version>0.9.9-RC1</version>
     </dependency>

     **/
    private CommandMapping()
    {
        mapping = new HashMap<String, Command>();
        try
        {
            String packageName = Command.class.getPackage().getName();
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

    public void setMapping(HashMap<String, Command> mapping)
    {
        this.mapping = mapping;
    }

    /**
     *  Double checked locking (since java 1.5+)
     **/
    public static HashMap<String, Command> getCommandMapping()
    {
        CommandMapping localInstance = instance;
        if(localInstance == null)
        {
            synchronized (CommandMapping.class)
            {
                localInstance = instance;
                if(localInstance == null)
                {
                    instance = localInstance = new CommandMapping();
                }
            }
        }
        return localInstance.getMapping();
    }

}
