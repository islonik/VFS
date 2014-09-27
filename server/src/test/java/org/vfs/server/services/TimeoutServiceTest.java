package org.vfs.server.services;

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
public class TimeoutServiceTest {

    @Autowired
    private NodeService nodeService;

    @Autowired
    public UserService userService;

    @Before
    public void setUp() throws InterruptedException, ParseException {
        nodeService.initDirs();

        UserSession nikita = userService.startSession();
        userService.attachUser(nikita.getUser().getId(), "nikita");
        Timer nikitaMockTimer = Mockito.mock(Timer.class);
        Mockito.when(nikitaMockTimer.difference()).thenReturn(15);
        nikita.setTimer(nikitaMockTimer);
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
        Socket nikitaMockSocket = Mockito.mock(Socket.class);
        Mockito.when(nikitaMockSocket.isClosed()).thenReturn(true);
        nikita.setSocket(nikitaMockSocket);

        UserSession empty = userService.startSession();
        Timer emptyMockTimer = Mockito.mock(Timer.class);
        Mockito.when(emptyMockTimer.difference()).thenReturn(10);
        empty.setTimer(emptyMockTimer);
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
        Socket emptyMockSocket = Mockito.mock(Socket.class);
        Mockito.when(emptyMockSocket.isClosed()).thenReturn(true);
        empty.setSocket(emptyMockSocket);

        UserSession r2d2 = userService.startSession();
        userService.attachUser(nikita.getUser().getId(), "r2d2");
        Timer r2d2MockTimer = Mockito.mock(Timer.class);
        Mockito.when(r2d2MockTimer.difference()).thenReturn(100);
        r2d2.setTimer(r2d2MockTimer);
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
        Socket r2d2MockSocket = Mockito.mock(Socket.class);
        Mockito.when(r2d2MockSocket.isClosed()).thenReturn(true);
        r2d2.setSocket(r2d2MockSocket);

        Map<String, UserSession> userSessions = new HashMap<>();
        userSessions.put(nikita.getUser().getId(), nikita);
        userSessions.put(empty.getUser().getId(), empty);
        userSessions.put(r2d2.getUser().getId(), r2d2);
    }

    @Test
    public void testTimeout() {
        TimeoutService timeoutService = new TimeoutService(userService, "1");
        timeoutService.timeout();

        Assert.assertEquals(0, userService.getRegistry().size());
    }
}
