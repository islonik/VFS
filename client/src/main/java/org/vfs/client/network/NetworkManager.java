package org.vfs.client.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Wait() / notifyAll() - notes:
 * When threads try to get socket(method getSocket()) then the threads will stop until the Main thread will open socket(method openSocket()).
 *
 * @author Lipatov Nikita
 */
public class NetworkManager {
    private volatile Socket socket;
    private MessageSender messageSender;

    public NetworkManager() {
    }

    public void openSocket(String serverHost, String serverPort) throws IOException {
        if (socket == null) {
            synchronized (this) {
                InetAddress ipAddress = InetAddress.getByName(serverHost);
                socket = new Socket(ipAddress, Integer.parseInt(serverPort));
                notifyAll();
            }
        }
    }

    public Socket getSocket() {
        if (socket == null) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("NetworkManager.getSocket().IOException.Message=" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
        return socket;
    }

    public void closeSocket() {
        try {
            if (socket != null) {
                Socket socket = this.socket;
                this.socket = null;
                socket.close();
            }
        } catch (IOException ioe) {
            System.err.println("NetworkManager.closeSocket().IOException.Message=" + ioe.getMessage());
        }
    }

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }


}
