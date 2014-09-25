package org.vfs.server.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Lipatov Nikita
 */
public class TimerTest {

    @Test
    public void testDifference() throws InterruptedException {

        Timer timer = new Timer();
        timer.updateTime();

        Assert.assertEquals(0, timer.difference());

        Thread.currentThread().sleep(60000); // 1 minute

        Assert.assertEquals(1, timer.difference());

    }
}
