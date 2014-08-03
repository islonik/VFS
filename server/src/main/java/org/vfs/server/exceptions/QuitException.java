package org.vfs.server.exceptions;

/**
 * @author Lipatov Nikita
 */
public class QuitException extends RuntimeException {

    public QuitException(String userLogin) {
        super("User " + userLogin + " has been disconnected!");
    }
}
