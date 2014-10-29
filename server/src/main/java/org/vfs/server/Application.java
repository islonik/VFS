package org.vfs.server;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Entry point
 *
 TODO: nio selecters?
 1) http://www.javaworld.com/article/2078654/java-se/five-ways-to-maximize-java-nio-and-nio-2.html
 2) Второй - протащить Socket в лиснеры и пусть они в цикле блокируются на них

 TODO 2:
 Возьми Google Protobuf
 Избавься от xml этого дуратского и перейди на него
 Не забудь maven плагин добавить для этого
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
