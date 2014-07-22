package org.vfs.client;

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
        Client client = new Client();
        client.run();
    }
}
