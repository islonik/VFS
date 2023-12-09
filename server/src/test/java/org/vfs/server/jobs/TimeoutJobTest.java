package org.vfs.server.jobs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.vfs.server.Application;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserSessionService;

/**
 * @author Lipatov Nikita
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = "test")
public class TimeoutJobTest {

    @Autowired
    private NodeService nodeService;

    @Autowired
    public UserSessionService userSessionService;

    @BeforeEach
    public void setUp() {
        nodeService.initDirs();

        // UserSession #1
        ClientWriter nikitaCWMock = Mockito.mock(ClientWriter.class);
        UserSession nikita = userSessionService.startSession(nikitaCWMock);

        userSessionService.attachUser(nikita.getUser().getId(), "nikita");

        // UserSession #2
        ClientWriter emptyCWMock = Mockito.mock(ClientWriter.class);
        UserSession empty = userSessionService.startSession(emptyCWMock);

        // UserSession #3
        ClientWriter r2d2CWMock = Mockito.mock(ClientWriter.class);

        UserSession r2d2 = userSessionService.startSession(r2d2CWMock);
        userSessionService.attachUser(r2d2.getUser().getId(), "r2d2");
    }

    @Test
    public void testTimeout() throws InterruptedException {
        TimeoutJob timeoutJob = new TimeoutJob(userSessionService, "1"); // 1 min

        Assertions.assertEquals(3, userSessionService.getRegistry().size());

        Thread.sleep(60000); // 1 min

        timeoutJob.timeout();

        Assertions.assertEquals(0, userSessionService.getRegistry().size());
    }
}
