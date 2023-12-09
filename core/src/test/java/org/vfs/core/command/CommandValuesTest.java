package org.vfs.core.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lipatov Nikita
 */
public class CommandValuesTest {

    @Test
    public void testCommandValues() {
        CommandValues values = new CommandValues();
        values.setCommand("copy");
        values.getParams().add("test");
        values.getParams().add("test2");

        Assertions.assertNull(values.getNextKey());
        Assertions.assertEquals("copy", values.getCommand());
        Assertions.assertEquals("test", values.getNextParam());
        Assertions.assertEquals("test2", values.getNextParam());
        Assertions.assertNull(values.getNextParam());
    }
}
