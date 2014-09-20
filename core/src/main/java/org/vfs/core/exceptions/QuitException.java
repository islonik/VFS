package org.vfs.core.exceptions;

/**
 * @author Lipatov Nikita
 */
public class QuitException extends RuntimeException {

    public QuitException(String quitMessage) {
        super(quitMessage);
    }
}
