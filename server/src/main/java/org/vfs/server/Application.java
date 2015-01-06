package org.vfs.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * Entry point
 *
 TODO: nio selecters?
 1) http://www.javaworld.com/article/2078654/java-se/five-ways-to-maximize-java-nio-and-nio-2.html
 2) Второй - протащить Socket в лиснеры и пусть они в цикле блокируются на них
 *
 * @author Lipatov Nikita
 */
public class Application {

    /**
     * @param args no arguments
     */
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");

        Server server = context.getBean(Server.class);
        server.run();

        /*ExecutorService es = Executors.newFixedThreadPool(1);
        es.submit(server);
        es.awaitTermination(Long.MAX_VALUE, DAYS);
        es.shutdown();*/
    }
}
