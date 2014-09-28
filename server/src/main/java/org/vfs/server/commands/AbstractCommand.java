package org.vfs.server.commands;

import org.vfs.server.network.ClientWriter;

import static org.vfs.core.network.protocol.Response.STATUS_OK;
import static org.vfs.core.network.protocol.Response.STATUS_FAIL;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
public abstract class AbstractCommand {

    protected volatile ClientWriter clientWriter;

    public void send(int status, String message) {
        clientWriter.send(
                newResponse(
                        status,
                        message
                )
        );
    }

    public void sendOK(String message) {
        clientWriter.send(
                newResponse(
                        STATUS_OK,
                        message
                )
        );
    }

    public void sendFail(String message) {
        clientWriter.send(
                newResponse(
                        STATUS_FAIL,
                        message
                )
        );
    }
}
