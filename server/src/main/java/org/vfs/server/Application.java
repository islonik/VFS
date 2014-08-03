package org.vfs.server;

import java.io.IOException;

/**
 * Entry point
 *
 * @author Lipatov Nikita
 */
public class Application {

    /**
     * @param args no arguments
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }
}
