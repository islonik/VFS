package org.vfs.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.vfs.core.exceptions.ValidationException;

/**
 * @author Lipatov Nikita
 */
public class PreconditionsTest {

    @Test(expected = ValidationException.class)
    public void testCheckNotNull_null() {
        Preconditions.checkNotNull(null, "Login is empty!");
    }

    @Test
    public void testCheckNotNull_notNull() {
        String login = "nikita";
        Preconditions.checkNotNull(login, "Login is empty!");
        Assert.assertTrue(true);
    }

    @Test
    public void testCheckArgument_true() {
        Preconditions.checkArgument(true, "Condition is true!");
    }

    @Test(expected = ValidationException.class)
    public void testCheckArgument_false() {
        Preconditions.checkArgument(false, "Condition is false!");
    }
}
