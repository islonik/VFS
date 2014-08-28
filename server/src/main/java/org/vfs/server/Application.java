package org.vfs.server;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Entry point
 * TODO: maven release plugin:
    + remove snapshot version and convert snapshot version to release version;
    + merge into master branch;
    + increase release version and convert release version to snapshot version;
   TODO: spring-develop branch (after maven-release-plugin)
    + class Server should be injected through spring injection system (xml configuration + annotations (classpath-scan!))
   // http://habrahabr.ru/post/231953/

 *
 * @author Lipatov Nikita
 */
public class Application {

    /**
     * @param args no arguments
     */
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("context/application.xml");

        Server server = (Server) context.getBean("Server");
        server.run();
    }
}
