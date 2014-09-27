package org.vfs.server.commands;

import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;

import static org.vfs.core.network.protocol.Response.STATUS_OK;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component("whoami")
public class Whoami implements Command {

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        ClientWriter clientWriter = userSession.getClientWriter();
        User user = userSession.getUser();
        clientWriter.send(
                newResponse(
                        STATUS_OK,
                        user.getLogin()
                )
        );
    }
}
