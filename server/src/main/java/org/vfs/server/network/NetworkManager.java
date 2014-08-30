package org.vfs.server.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lipatov Nikita
 */
@Component
public class NetworkManager {
    private final ServerSocket serverSocket;
    private final List<Socket> sockets;

    @Autowired
    public NetworkManager(
            @Value("${server.name}") String address,
            @Value("${server.port}") String port,
            @Value("${server.pool}") String pool) throws IOException {

        InetAddress inetAddress = InetAddress.getByName(address);
        serverSocket = new ServerSocket(Integer.parseInt(port), Integer.parseInt(pool), inetAddress);
        sockets = new ArrayList<>();
    }

    public Socket accept() throws IOException {
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
    }

}
