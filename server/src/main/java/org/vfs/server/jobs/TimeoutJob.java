package org.vfs.server.jobs;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vfs.core.network.protocol.Protocol;
import org.vfs.core.network.protocol.ResponseFactory;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.UserService;

/**
 * @author Lipatov Nikita
 */
@Component
@EnableScheduling
public class TimeoutJob {

    private final UserService userService;
    private final int timeout;

    @Autowired
    public TimeoutJob(UserService userService, @Value("${server.timeout}") String timeout) {
        this.userService = userService;
        this.timeout = Integer.parseInt(timeout);
    }

    /**
     * According to the Quartz-Scheduler Tutorial :
     * The field order of the cronExpression is
     * 1.Seconds
     * 2.Minutes
     * 3.Hours
     * 4.Day-of-Month
     * 5.Month
     * 6.Day-of-Week
     * 7.Year (optional field)
     * Ensure you have at least 6 parameters or you will get an error (year is optional)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void timeout() {
        Map<String, UserSession> sessions = userService.getRegistry();
        Set<String> keySet = sessions.keySet();
        for(String key : keySet) {
            UserSession userSession = sessions.get(key);
            String login = userSession.getUser().getLogin();
            int diff = userSession.getTimer().difference();
            System.out.println("key = " + key + " login = " + login + " diff = " + diff);
            if(diff >= 1 && (login == null || login.trim().isEmpty())) { // very rare case
                System.out.println("Null thread was killed!");
                userService.stopSession(key);
            }
            if(diff >= timeout && login != null && !login.trim().isEmpty()) { // kill session
                System.out.println("Thread was killed!");

                userSession.getClientWriter().send(
                        ResponseFactory.newResponse(
                                Protocol.Response.ResponseType.SUCCESS_QUIT,
                                "Timeout disconnect"
                        )
                );

                userService.notifyUsers(
                        userSession.getUser().getId(),
                        "User " + userSession.getUser().getLogin() + " was disconnected from server by timeout!"
                );

                userService.stopSession(key);
            }
        }
    }

}
