package org.vfs.server.services;

import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.UserSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Lipatov Nikita
 */
public class UserService {
    private NodeService nodeService;
    private Map<String, UserSession> registry = new HashMap<String, UserSession>();

    public UserService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public UserSession startSession() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());

        UserSession userSession = new UserSession();
        userSession.setUser(user);

        registry.put(user.getId(), userSession);
        return userSession;
    }

    public void attachUser(String id, String login) {
        UserSession userSession = registry.get(id);

        Node home = nodeService.createHomeDirectory(login);
        userSession.setNode(home);
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

    public UserSession stopSession(String id) {
        return registry.remove(id);
    }


}
