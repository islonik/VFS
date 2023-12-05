package org.vfs.server.jobs;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vfs.core.network.protocol.Protocol.Response;
import org.vfs.core.network.protocol.ResponseFactory;
import org.vfs.server.model.UserSession;
import org.vfs.server.services.UserSessionService;

/**
 * @author Lipatov Nikita
 */
@Component
@EnableScheduling
public class TimeoutJob {

    private final UserSessionService userSessionService;
    private final int timeout;

    @Autowired
    public TimeoutJob(UserSessionService userSessionService, @Value("${server.timeout}") String timeout) {
        this.userSessionService = userSessionService;
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
        Map<String, UserSession> sessions = userSessionService.getRegistry();
        Set<String> keySet = sessions.keySet();
        for(String key : keySet) {
            UserSession userSession = sessions.get(key);
            String login = userSession.getUser().getLogin();
            int diff = userSession.getTimer().difference();
            System.out.println("DEBUG: " + "key = " + key + " login = " + login + " diff = " + diff);
            if(diff >= 1 && Strings.isNullOrEmpty(login)) { // empty session case
                System.out.println("Empty session was killed!");
                userSessionService.stopSession(key);
            }
            if(diff >= timeout && !Strings.isNullOrEmpty(login)) { // kill session
                System.out.println("DEBUG: " + "Connection was killed!");

                userSession.getClientWriter().send(
                        ResponseFactory.newResponse(
                                Response.ResponseType.SUCCESS_QUIT,
                                "Your session was terminated by timeout. Please login again!"
                        )
                );

                userSessionService.notifyUsers(
                        userSession.getUser().getId(),
                        "User " + userSession.getUser().getLogin() + " was disconnected from server by timeout!"
                );

                userSessionService.stopSession(key);
            }
        }
    }

}
