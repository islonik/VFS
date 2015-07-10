package org.vfs.server.services;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.vfs.core.network.protocol.ResponseFactory.newResponse;

/**
 * @author Lipatov Nikita
 */
@Component
public class UserSessionService {
    private final NodeService nodeService;
    private final LockService lockService;

    private final Map<String, UserSession> registry = new ConcurrentHashMap<>();

    @Autowired
    public UserSessionService(NodeService nodeService, LockService lockService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
    }

    public UserSession startSession(ClientWriter clientWriter) {
        String sessionId = UUID.randomUUID().toString();
        Protocol.User user = Protocol.User.newBuilder()
                .setId(sessionId)
                .setLogin("")
                .build();

        UserSession userSession = new UserSession(user, clientWriter);

        registry.put(sessionId, userSession);
        return userSession;
    }

    public void attachUser(String id, String login) {
        UserSession userSession = registry.get(id);

        Node loginHome = nodeService.createHomeDirectory(login);

        userSession.setNode(loginHome);
        Protocol.User user = userSession.getUser();
        user = user.toBuilder().setLogin(login).build();
        userSession.setUser(user);
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
     * @param id
     */
    public void stopSession(String id) {
        UserSession userSession = registry.remove(id);
        if (userSession != null) { // can be null
            String login = userSession.getUser().getLogin();
            if (!Strings.isNullOrEmpty(login)) { // can be null or empty
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
            if (!userSession.getUser().getId().equals(idMySession) && !Strings.isNullOrEmpty(login)) { // to all users except mine and null sessions
                ClientWriter clientWriter = userSession.getClientWriter();
                clientWriter.send(
                        newResponse(
                                Protocol.Response.ResponseType.OK,
                                message
                        )
                );
            }
        }
    }


}
