package org.vfs.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;

import static java.nio.channels.SelectionKey.OP_ACCEPT;

/**
 * @author Lipatov Nikita
 */
@Component
public class NetworkManager {
    private static final Logger log = LoggerFactory.getLogger(NetworkManager.class);

    private final String address;
    private final int port;
    private final int pool;
    /*private final ServerSocket serverSocket;
    private final List<Socket> sockets;*/

    @Autowired
    public NetworkManager(
            @Value("${server.name}") String address,
            @Value("${server.port}") String port,
            @Value("${server.pool}") String pool) throws IOException {
        this.address = address;
        this.port = Integer.parseInt(port);
        this.pool = Integer.parseInt(pool);

        /*InetAddress inetAddress = InetAddress.getByName(address);
        serverSocket = new ServerSocket(this.port, this.pool, inetAddress);
        sockets = new ArrayList<>();*/
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

    /*public Socket accept() throws IOException {
        Socket socket = serverSocket.accept();
        sockets.add(socket);
        return socket;
    }

    public void disconnect() {
        try {
            for (Socket socket : sockets) {
                socket.close();
            }
        } catch (IOException ioe) {
            System.err.println("NetworkManager.closeSocket().IOException.Message=" + ioe.getMessage());
        }
    }*/

}
