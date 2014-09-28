package org.vfs.server.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.utils.NodePrinter;

/**
 * @author Lipatov Nikita
 */
@Component("print")
public class Print extends AbstractCommand implements Command {

    private final NodePrinter nodePrinter;

    @Autowired
    public Print(NodePrinter nodePrinter) {
        this.nodePrinter = nodePrinter;
    }

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        Node directory = userSession.getNode();

        sendOK(nodePrinter.print(directory));
    }
}
