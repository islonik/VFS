package org.vfs.client.network;

import com.google.protobuf.InvalidProtocolBufferException;
import org.vfs.core.network.protocol.Protocol;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

/**
 * SocketReader should listen socket(inputStream) and write message in queue
 * BlockingQueue should use non-blocking API.
 *
 * @author Lipatov Nikita
 */
public class SocketReader {
    private final BlockingQueue<Protocol.Response> toUserQueue;
    private final NetworkManager networkManager;

    public SocketReader(BlockingQueue<Protocol.Response> queue, NetworkManager networkManager) throws IOException {
        this.toUserQueue = queue;
        this.networkManager = networkManager;
    }

    public void run() {
        try {
            while (true) {
                try {
                    Protocol.Response response = Protocol.Response.parseDelimitedFrom(networkManager.getSocket().getInputStream());
                    this.toUserQueue.put(response);
                } catch (SocketException | InvalidProtocolBufferException se) {
                    if(!se.getMessage().toLowerCase().equals("socket closed")) {
                        System.err.println("SocketReader.SocketException.Message=" + se.getMessage());
                        throw new RuntimeException(se);
                    }
                } catch (IOException ioe) {
                    System.err.println("SocketReader.IOException.Message=" + ioe.getMessage());
                    throw new RuntimeException(ioe);
                }
            }
        } catch (InterruptedException ie) {
            System.err.println("SocketReader.InterruptedException.Message=" + ie.getMessage());
        }
    }

}
