package org.vfs.server.user;

/**
 * @author Lipatov Nikita
 */
public class GeneratorID
{
    private static final GeneratorID instance = new GeneratorID();
    private String id;
    private long count;

    private GeneratorID()
    {
        count = 0;
    }

    public static GeneratorID getInstance()
    {
        return instance;
    }

    public String getId()
    {
        long time = System.currentTimeMillis();
        return time + "" + Integer.toString(Thread.activeCount()) + "" + Long.toString(count++);
    }


}
