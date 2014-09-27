package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.utils.NodePrinter;

import static org.vfs.core.network.protocol.Response.STATUS_OK;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("print")
public class Print implements Command {

    private final NodePrinter nodePrinter;

    @Autowired
    public Print(NodePrinter nodePrinter) {
        this.nodePrinter = nodePrinter;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        ClientWriter clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();
        clientWriter.send(
                newResponse(
                        STATUS_OK,
                        nodePrinter.print(directory)
                )
        );
    }
}
