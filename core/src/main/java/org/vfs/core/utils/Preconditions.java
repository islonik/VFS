package org.vfs.core.utils;

import org.vfs.core.exceptions.ValidationException;

/**
 * @author Lipatov Nikita
 */
public class Preconditions {

    public static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new ValidationException(message);
        }
    }

    public static void checkArgument(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }
}
