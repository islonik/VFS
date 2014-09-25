package org.vfs.server.model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Lipatov Nikita
 */
public class Timer {

    private volatile Date lastMessageDate;

    public Timer() {
        lastMessageDate = new Date();
    }

    public void updateTime() {
        lastMessageDate = new Date();
    }

    /**
     *
     * @return number of minutes
     */
    public int difference() {
        long diff = new Date().getTime() - lastMessageDate.getTime();
        diff = TimeUnit.MILLISECONDS.toMinutes(diff);
        return (int) diff;
    }
}
