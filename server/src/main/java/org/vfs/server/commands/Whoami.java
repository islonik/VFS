package org.vfs.server.commands;

import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.core.network.protocol.proto.RequestProto;
import org.vfs.server.model.UserSession;

/**
 * @author Lipatov Nikita
 */
@Component("whoami")
public class Whoami extends AbstractCommand implements Command {

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        RequestProto.Request.User user = userSession.getUser();

        sendOK(user.getLogin());
    }
}
