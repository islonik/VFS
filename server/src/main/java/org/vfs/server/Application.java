package org.vfs.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Entry point
 *
 * @author Lipatov Nikita
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {
        "org.vfs.server"
})
public class Application {

    public static void main(String[] args){
        log.info("VFS is starting...");
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args)
                .registerShutdownHook();
    }


}
