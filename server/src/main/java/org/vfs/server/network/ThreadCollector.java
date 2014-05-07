package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class determines the time of thread life.
 * @author Lipatov Nikita
 */
class ThreadCollector
{

    private static final Logger log = LoggerFactory.getLogger(ThreadCollector.class);
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private ServerThread thread = null;
    private Timer timer = null;
    private int count = 0;
    private boolean isNoFirstMessage = true;

    public ThreadCollector(ServerThread thread)
    {
        this.thread = thread;
        this.setTimeout(Integer.parseInt(ServerSettings.getInstance().getTimeout()));
    }

    /**
     * Method sets the time of life
     * @param minutes Minutes of life.
     */
    private void setTimeout(int minutes)
    {
        int delay = (minutes * MINUTE) / 10;
        timer = new Timer
        (
            delay,
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent evt)
                {
                    run();
                }
            }
        );

        timer.start();
    }

    /**
     * Start counntdown.
     */
    public void run()
    {
        if (count++ == 10 || isNoFirstMessage)
        {
            String name = thread.getName();
            if(name != null && !name.isEmpty())
            {
                log.info("kill connection with name = " + name);
            }
            else
            {
                log.info("kill connection with id = " + thread.getID());
            }
            thread.kill();
        }
    }

    public void resetTimer()
    {
        isNoFirstMessage = false;
        count = 0;
    }

    public void stop()
    {
        timer.stop();
    }
}
