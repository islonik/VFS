package org.vfs.client.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Wait() / notifyAll() - notes:
 * When threads try to get socket(method getSocket()) then the threads will stop until the Main thread will open socket(method openSocket()).
 * + double checked locking
 * @author Lipatov Nikita
 */
public class NetworkManager {
    private volatile SocketChannel client;
    private volatile Selector selector;
    private MessageSender messageSender;

    public NetworkManager() {
    }

    public void openSocket(String serverHost, String serverPort) throws IOException {
        if (client == null) {
            synchronized (this) {
                while(client == null) {
                    client = SocketChannel.open();
                    // nonblocking I/O
                    client.configureBlocking(false);
                    client.connect(new InetSocketAddress(serverHost, Integer.parseInt(serverPort)));
                    selector = Selector.open();
                    client.register(selector, SelectionKey.OP_CONNECT);

                    notifyAll();
                }
            }
        }
    }

    public Selector getSelector() {
        if (selector == null) {
            synchronized (this) {
                while(selector == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.err.println("NetworkManager.getSocket().IOException.Message=" + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return selector;
    }

    public SocketChannel getSocketChannel() {
        if (client == null) {
            synchronized (this) {
                while(client == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.err.println("NetworkManager.getSocket().IOException.Message=" + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return client;
    }

    public void closeSocket() {
        try {
            if (client != null) {
                synchronized (this) {
                    while(client != null) {
                        selector.close();
                        client.socket().close();
                        client.close();
                        client = null;
                        selector = null;
                    }
                }
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
