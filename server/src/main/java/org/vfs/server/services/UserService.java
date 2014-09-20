package org.vfs.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.core.network.protocol.User;
import org.vfs.server.model.Node;
import org.vfs.server.model.NodeTypes;
import org.vfs.server.model.UserSession;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lipatov Nikita
 */
@Component
public class UserService {
    private final NodeService nodeService;
    private final Map<String, UserSession> registry = new ConcurrentHashMap<>();

    @Autowired
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

        Node home = nodeService.getHome();
        Node loginHome = nodeService.getNodeManager().newNode(login, NodeTypes.DIR);
        nodeService.getNodeManager().setParent(loginHome, home);

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

    public UserSession stopSession(String id) {
        return registry.remove(id);
    }


}
