package org.vfs.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vfs.core.exceptions.ValidationException;

/**
 * @author Lipatov Nikita
 */
public class PreconditionsTest {

    @Test
    public void testCheckNotNullWithNull() {
        String message = "Login is empty!";
        ValidationException thrown = Assertions.assertThrows(
                ValidationException.class,
                () -> Preconditions.checkNotNull(null, message),
                message
        );

        Assertions.assertEquals(message, thrown.getMessage());
    }

    @Test
    public void testCheckNotNullWithNotNull() {
        String login = "nikita";
        Preconditions.checkNotNull(login, "Login is empty!");
        Assertions.assertTrue(true);
    }

    @Test
    public void testCheckArgumentConditionIsTrue() {
        Preconditions.checkArgument(true, "Condition is true!");
    }

    @Test
    public void testCheckArgumentConditionIsFalse() {
        String message = "Condition is false!";
        ValidationException thrown = Assertions.assertThrows(
                ValidationException.class,
                () -> Preconditions.checkArgument(false, message),
                message
        );

        Assertions.assertEquals(message, thrown.getMessage());
    }
}
