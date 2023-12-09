package org.vfs.server.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lipatov Nikita
 */
public class TimerTest {

    @Test
    public void testDifference() throws InterruptedException {
        Timer timer = new Timer();
        timer.updateTime();

        Assertions.assertEquals(0, timer.difference());

        Thread.sleep(60000); // 1 minute

        Assertions.assertEquals(1, timer.difference());
    }
}
