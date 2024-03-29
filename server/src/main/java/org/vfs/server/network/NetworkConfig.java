package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Lipatov Nikita
 */
@Component
public class NetworkConfig {
    private final String address;
    private final int port;
    private final int pool;

    @Autowired
    public NetworkConfig(
            @Value("${server.name}") String address,
            @Value("${server.port}") String port,
            @Value("${server.pool}") String pool) {
        this.address = address;
        this.port = Integer.parseInt(port);
        this.pool = Integer.parseInt(pool);
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getPool() {
        return pool;
    }
}
