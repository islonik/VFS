package org.vfs.server.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lipatov Nikita
 */
public class NetworkManager {
    private final ServerSocket serverSocket;
    private final List<Socket> sockets;

    public NetworkManager(String address, int port, int connectionPool) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(address);
        serverSocket = new ServerSocket(port, connectionPool, inetAddress);
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
