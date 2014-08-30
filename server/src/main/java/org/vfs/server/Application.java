package org.vfs.server;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");

        Server server = context.getBean(Server.class);
        server.run();
    }
}
