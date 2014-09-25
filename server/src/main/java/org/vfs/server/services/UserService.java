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
    private final LockService lockService;

    private final Map<String, UserSession> registry = new ConcurrentHashMap<>();

    @Autowired
    public UserService(NodeService nodeService, LockService lockService) {
        this.nodeService = nodeService;
        this.lockService = lockService;
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
        if(registry.containsKey(id)) {
            UserSession userSession = registry.get(id);
            String login = userSession.getUser().getLogin();
            if(login != null) { // sessions without user
                this.nodeService.removeHomeDirectory(login);
                lockService.unlockAll(userSession.getUser());
            }
            registry.remove(id);
        }
    }

    public final Map<String, UserSession> getRegistry() {
        return registry;
    }


}
