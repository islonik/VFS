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
import org.vfs.server.model.Timer;
import org.vfs.server.model.UserSession;
import org.vfs.server.network.ClientWriter;
import org.vfs.server.services.NodeService;
import org.vfs.server.services.UserService;

import java.net.Socket;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

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
    public UserService userService;

    @Before
    public void setUp() throws InterruptedException, ParseException {
        nodeService.initDirs();

        // UserSession #1
        Socket nikitaSocketMock = Mockito.mock(Socket.class);
        Mockito.when(nikitaSocketMock.isClosed()).thenReturn(true);
        Timer nikitaTimerMock = Mockito.mock(Timer.class);
        Mockito.when(nikitaTimerMock.difference()).thenReturn(15);
        ClientWriter nikitaCWMock = Mockito.mock(ClientWriter.class);

        UserSession nikita = userService.startSession(nikitaSocketMock, nikitaTimerMock, nikitaCWMock);
        userService.attachUser(nikita.getUser().getId(), "nikita");
        nikita.setTask(new RunnableFuture() {
            @Override
            public void run() {

            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        });

        // UserSession #2
        Socket emptySocketMock = Mockito.mock(Socket.class);
        Mockito.when(emptySocketMock.isClosed()).thenReturn(true);
        Timer emptyTimerMock = Mockito.mock(Timer.class);
        Mockito.when(emptyTimerMock.difference()).thenReturn(10);
        ClientWriter emptyCWMock = Mockito.mock(ClientWriter.class);

        UserSession empty = userService.startSession(emptySocketMock, emptyTimerMock, emptyCWMock);
        empty.setTask(new Future() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        });

        // UserSession #3
        Socket r2d2SocketMock = Mockito.mock(Socket.class);
        Mockito.when(r2d2SocketMock.isClosed()).thenReturn(true);
        Timer r2d2TimerMock = Mockito.mock(Timer.class);
        Mockito.when(r2d2TimerMock.difference()).thenReturn(100);
        ClientWriter r2d2CWMock = Mockito.mock(ClientWriter.class);

        UserSession r2d2 = userService.startSession(r2d2SocketMock, r2d2TimerMock, r2d2CWMock);
        userService.attachUser(r2d2.getUser().getId(), "r2d2");
        r2d2.setTask(new Future() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        });

        Map<String, UserSession> userSessions = new HashMap<>();
        userSessions.put(nikita.getUser().getId(), nikita);
        userSessions.put(empty.getUser().getId(), empty);
        userSessions.put(r2d2.getUser().getId(), r2d2);
    }

    @Test
    public void testTimeout() {
        TimeoutJob timeoutJob = new TimeoutJob(userService, "1");
        timeoutJob.timeout();

        Assert.assertEquals(0, userService.getRegistry().size());
    }
}
