package org.vfs.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vfs.client.network.IncomingMessageHandler;
import org.vfs.client.network.MessageSender;
import org.vfs.client.network.UserManager;
import org.vfs.client.network.NetworkManager;
import org.vfs.core.exceptions.QuitException;
import org.vfs.core.network.protocol.GenericMarshalling;
import org.vfs.core.network.protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Lipatov Nikita
 */
public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private final NetworkManager networkManager;
    private final UserManager userManager;

    public Client() throws IOException {

        userManager = new UserManager();
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        final BlockingQueue<Protocol.Response> toUserQueue = new ArrayBlockingQueue<Protocol.Response>(1024);
        final BlockingQueue<Protocol.Request> toServerQueue = new ArrayBlockingQueue<Protocol.Request>(1024);

        final MessageSender messageSender = new MessageSender(toServerQueue);
        networkManager = new NetworkManager();
        networkManager.setMessageSender(messageSender);

        final IncomingMessageHandler incomingMessageHandler = new IncomingMessageHandler(userManager, networkManager);

        // loop
        executorService.execute(
            new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        Selector selector = networkManager.getSelector();
                        SocketChannel socketChannel = networkManager.getSocketChannel();

                        try {
                            while(true) {
                                selector.select();
                                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                                while(keys.hasNext()) {
                                    SelectionKey key = keys.next();
                                    keys.remove();

                                    // Get the socket channel held by the key
                                    SocketChannel channel = (SocketChannel)key.channel();

                                    // Attempt a connection
                                    if (key.isConnectable()) { // connect command

                                        // Close pendent connections
                                        if (channel.isConnectionPending()) {
                                            channel.finishConnect();
                                        }

                                        Protocol.Request request = toServerQueue.take();

                                        channel.write(GenericMarshalling.objectToByteBuffer(request));

                                        socketChannel.register(selector, SelectionKey.OP_READ);

                                    } else if(key.isReadable()) {
                                        Protocol.Response response = null;
                                        ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);
                                        int numRead = -1;

                                        try {
                                            numRead = channel.read(buffer); // get message from client

                                            if(numRead == -1) {
                                                log.debug("Connection closed by: {}", channel.getRemoteAddress());
                                                channel.close();
                                            }

                                            byte[] data = new byte[numRead];
                                            System.arraycopy(buffer.array(), 0, data, 0, numRead);
                                            response = (Protocol.Response) GenericMarshalling.objectFromByteBuffer(data);
                                        } catch (Exception e) {
                                            log.error("Unable to read from channel", e);
                                            try {
                                                channel.close();
                                            } catch (IOException e1) {
                                                //nothing to do, channel dead
                                            }
                                        }
                                        if(response == null) {
                                            System.err.println("Response to server was not generated!");
                                        }
                                        incomingMessageHandler.handle(response);

                                        socketChannel.register(selector, SelectionKey.OP_WRITE);
                                    } else if(key.isWritable()) {
                                        Protocol.Request request = toServerQueue.take();
                                        channel.write(GenericMarshalling.objectToByteBuffer(request));

                                        socketChannel.register(selector, SelectionKey.OP_READ);
                                    }
                                }
                            }
                        } catch (IOException | InterruptedException | QuitException err) {
                            userManager.setUser(null);
                            networkManager.closeSocket();

                            if(!(err instanceof QuitException)) {
                                System.err.println(err.getMessage());
                            }
                        }
                    }
                }
            }
        );

    }

    public void run() {
        try
        (
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        ) {
            CommandLine commandLine = new CommandLine(userManager, networkManager);

            String clientHello =
                    "The VFS client is run.\n" +
                    "To connect for the server, please enter next command:\n" +
                    "Type 'connect server_name:port UserName' for connecting to the VFS server\n" +
                    "Type 'quit' command for disconnecting from the VFS server.\n" +
                    "Type 'exit' command for closing client.\n";
            System.out.println(clientHello);

            while (true) {
                try {
                    String inputCommand = keyboard.readLine().trim();

                    commandLine.execute(inputCommand);
                } catch (IOException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        } catch (QuitException qe) {
            System.out.println(qe.getMessage());
        } catch (Exception error) {
            System.err.println(error.getLocalizedMessage());
        } finally {
            System.exit(1);
        }
    }
}
