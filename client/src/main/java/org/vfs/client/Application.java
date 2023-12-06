package org.vfs.client;

/**
 * Entry point
 *
 * @author Lipatov Nikita
 */
public class Application {

    /**
     * @param args no arguments
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
