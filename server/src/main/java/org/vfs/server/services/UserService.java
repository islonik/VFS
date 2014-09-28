package org.vfs.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.Timer;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;

import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.vfs.core.network.protocol.Response.STATUS_OK;
import static org.vfs.core.network.protocol.Response.STATUS_SUCCESS_QUIT;
import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component
public class UserService {
    private final NodeService nodeService;
    private final LockService lockService;

    private final Map<String, UserSession> registry = new ConcurrentHashMap<>();

    @Autowired
    public UserService(NodeService nodeService, LockService lockService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
    }

    public UserSession startSession(Socket socket, Timer timer, ClientWriter clientWriter) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());

        UserSession userSession = new UserSession(user, socket, timer, clientWriter);

        registry.put(user.getId(), userSession);
        return userSession;
    }

    public void attachUser(String id, String login) {
        UserSession userSession = registry.get(id);

        Node loginHome = nodeService.createHomeDirectory(login);

        userSession.setNode(loginHome);
        userSession.getUser().setLogin(login);
    }

    public boolean isLogged(String login) {
        Set<String> keySet = registry.keySet();
        for (String key : keySet) {
            UserSession userSession = registry.get(key);
            String userLogin = userSession.getUser().getLogin();
            if (userLogin != null && userLogin.equals(login)) {
                return true;
            }
        }
        return false;
    }

    public UserSession getSession(String id) {
        return registry.get(id);
    }

    /**
     * Do not kill thread.
     * @param id
     */
    public void stopSession(String id) {
        UserSession userSession = registry.remove(id);
        if(userSession != null) { // can be null
            String login = userSession.getUser().getLogin();
            if(login != null) { // can be null
                nodeService.removeHomeDirectory(login);
                lockService.unlockAll(userSession.getUser());
            }
        }
    }

    public final Map<String, UserSession> getRegistry() {
        return registry;
    }

    public void notifyUsers(String idMySession, String message) {
        Set<String> keySet = registry.keySet();
        for (String key : keySet) {
            UserSession userSession = registry.get(key);
            String login = userSession.getUser().getLogin();
            if(!userSession.getUser().getId().equals(idMySession) && login != null) { // to all users except mine
                ClientWriter clientWriter = userSession.getClientWriter();
                clientWriter.send(newResponse(STATUS_OK, message));
            }
        }
    }


}
