package org.vfs.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.vfs.server.services.NodeService;

/**
 * Entry point
 *
 * @author Lipatov Nikita
 */
public class Application {

    /**
     * @param args no arguments
     */
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");

        NodeService nodeService = context.getBean(NodeService.class);
        nodeService.initDirs();

        Server server = context.getBean(Server.class);
        server.run();
    }
}
