package org.vfs.server.commands;

import org.springframework.stereotype.Component;
import org.vfs.core.command.CommandValues;
import org.vfs.server.model.UserSession;

/**
 * @author Lipatov Nikita
 */
@Component("help")
public class Help extends AbstractCommand implements Command {

    @Override
    public void apply(UserSession userSession, CommandValues values) {
        clientWriter = userSession.getClientWriter();
        sendOK("You can use next commands:\n" +
                "    * - cd directory \n" +
                "    * - connect server_name:port login \n" +
                "    * - copy node directory \n" +
                "    * - help \n" +
                "    * - lock [-r] node \n" +
                "        -r - enable recursive mode \n" +
                "    * - mkdir directory \n" +
                "    * - mkfile file\n" +
                "    * - move node directory \n" +
                "    * - print \n" +
                "    * - quit \n" +
                "    * - rename node name \n" +
                "    * - rm node \n" +
                "    * - unlock [-r] node \n" +
                "        -r - enable recursive mode \n"
        );
    }
}
