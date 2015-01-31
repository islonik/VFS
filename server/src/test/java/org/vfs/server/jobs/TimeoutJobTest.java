package org.vfs.server.jobs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserSessionService;

import java.text.ParseException;

/**
 * @author Lipatov Nikita
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TimeoutJobTest {

    @Autowired
    private NodeService nodeService;

    @Autowired
    public UserSessionService userSessionService;

    @Before
    public void setUp() throws InterruptedException, ParseException {
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

        Assert.assertEquals(3, userSessionService.getRegistry().size());

        Thread.sleep(60000); // 1 min

        timeoutJob.timeout();

        Assert.assertEquals(0, userSessionService.getRegistry().size());
    }
}
