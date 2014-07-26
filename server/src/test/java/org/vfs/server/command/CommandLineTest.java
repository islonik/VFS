package org.vfs.server.command;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.vfs.core.network.protocol.User;
import org.vfs.server.CommandLine;
import org.vfs.server.command.impl.*;
import org.vfs.core.model.Context;
import org.vfs.server.model.Partition;
import org.vfs.server.model.impl.Directory;
import org.vfs.server.user.UserSession;

/**
 * @author: Lipatov Nikita
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommandLineTest
{

    private static User userOne;
    private static User userTwo;

    @BeforeClass
    public static void before()
    {
        Partition.cleanup();
        UserSession.cleanup();
    }

    @Test
    public void testCommandLine_testCase01()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.execute(null, "connect user1");
        Assert.assertTrue(context.isCommandWasExecuted());
        userOne = context.getUser();
        Assert.assertNotNull(userOne);
        Assert.assertEquals("/home/user1", ((Directory)userOne.getDirectory()).getFullPath());

        context = cmd.execute(null, "connect user1");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertNull(context.getUser());
        Assert.assertEquals(ConnectCommand.USER_ALREADY_EXIST, context.getMessage());

        context = cmd.execute(null, "connect user2");
        Assert.assertTrue(context.isCommandWasExecuted());
        userTwo = context.getUser();
        Assert.assertNotNull(userTwo);
        Assert.assertEquals("/home/user2", ((Directory)userTwo.getDirectory()).getFullPath());

        context = cmd.execute(userTwo, "quit");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userTwo, "quit");
        Assert.assertFalse(context.isCommandWasExecuted());

        context = cmd.execute(null, "connect user2");
        Assert.assertTrue(context.isCommandWasExecuted());
    }

    @Test
    public void testCommandLine_testCase02()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.execute(userOne, "cd u034");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(ChangeDirectoryCommand.NODE_NOT_FOUND, context.getMessage());

        context = cmd.execute(userOne, "cd ../..");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals("/", ((Directory)context.getUser().getDirectory()).getFullPath());

        context = cmd.execute(userTwo, "cd ../..");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals("/", ((Directory)context.getUser().getDirectory()).getFullPath());

        context = cmd.execute(userOne, "mkdir applications");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals("/", ((Directory)context.getUser().getDirectory()).getFullPath());

        context = cmd.execute(userOne, "mkdir applications");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MakeDirectoryCommand.DIRECTORY_NOT_CREATED, context.getMessage());

        context = cmd.execute(userOne, "mkdir logs");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "mkdir u01");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "mkdir u02");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "mkdir u02/toms");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "print");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals
        (
            "/\n" +
            "|__applications\n" +
            "|__home\n" +
            "|  |__user1\n" +
            "|  |__user2\n" +
            "|__logs\n" +
            "|__u01\n" +
            "|__u02\n" +
            "|  |__toms\n",
            context.getMessage()
        );
    }

    @Test
    public void testCommandLine_testCase03()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.execute(userOne, "mkfile applications/result.log");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "mkfile applications/result.log");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MakeFileCommand.FILE_NOT_CREATED, context.getMessage());

        context = cmd.execute(userOne, "mkfile u01/config.u01.log");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "cd u02");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "mkfile config.u02.log");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "cd ..");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "print");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals
        (
            "/\n" +
            "|__applications\n" +
            "|  |__result.log\n" +
            "|__home\n" +
            "|  |__user1\n" +
            "|  |__user2\n" +
            "|__logs\n" +
            "|__u01\n" +
            "|  |__config.u01.log\n" +
            "|__u02\n" +
            "|  |__toms\n" +
            "|  |__config.u02.log\n",
            context.getMessage()
        );
    }

    @Test
    public void testCommandLine_testCase04()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.execute(userOne, "cd applications");
        context = cmd.execute(userOne, "cd result.log");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals("/applications", ((Directory)context.getUser().getDirectory()).getFullPath());
        Assert.assertEquals(ChangeDirectoryCommand.NODE_IS_FILE, context.getMessage());

        context = cmd.execute(userOne, "cd ..");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "print");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals
        (
            "/\n" +
            "|__applications\n" +
            "|  |__result.log\n" +
            "|__home\n" +
            "|  |__user1\n" +
            "|  |__user2\n" +
            "|__logs\n" +
            "|__u01\n" +
            "|  |__config.u01.log\n" +
            "|__u02\n" +
            "|  |__toms\n" +
            "|  |__config.u02.log\n",
            context.getMessage()
        );
    }

    @Test
    public void testCommandLine_testCase05()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.execute(userOne, "copy u03 u01");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(CopyCommand.SOURCE_NOT_FOUND, context.getMessage());

        context = cmd.execute(userOne, "copy u01 u04");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(CopyCommand.DESTINATION_NOT_FOUND, context.getMessage());

        context = cmd.execute(userOne, "copy applications u02/config.u02.log");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(CopyCommand.DESTINATION_NOT_DIRECTORY, context.getMessage());

        context = cmd.execute(userOne, "copy u01 u02/toms");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "print");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals
        (
            "/\n" +
            "|__applications\n" +
            "|  |__result.log\n" +
            "|__home\n" +
            "|  |__user1\n" +
            "|  |__user2\n" +
            "|__logs\n" +
            "|__u01\n" +
            "|  |__config.u01.log\n" +
            "|__u02\n" +
            "|  |__toms\n" +
            "|  |  |__u01\n" +
            "|  |  |  |__config.u01.log\n" +
            "|  |__config.u02.log\n",
            context.getMessage()
        );
    }

    @Test
    public void testCommandLine_testCase06()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.execute(userOne, "lock u01");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "lock u03");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(LockCommand.NODE_NOT_FOUND, context.getMessage());

        context = cmd.execute(userOne, "lock u01/config.u01.log");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "rm u01/config.u01.log");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(RemoveCommand.NODES_IS_LOCKED, context.getMessage());

        context = cmd.execute(userTwo, "lock u01/config.u01.log");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(LockCommand.NODE_NOT_LOCKED, context.getMessage());

        context = cmd.execute(userOne, "print");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals
        (
            "/\n" +
            "|__applications\n" +
            "|  |__result.log\n" +
            "|__home\n" +
            "|  |__user1\n" +
            "|  |__user2\n" +
            "|__logs\n" +
            "|__u01 [Locked by user1 ]\n" +
            "|  |__config.u01.log [Locked by user1 ]\n" +
            "|__u02\n" +
            "|  |__toms\n" +
            "|  |  |__u01\n" +
            "|  |  |  |__config.u01.log\n" +
            "|  |__config.u02.log\n",
            context.getMessage()
        );

        context = cmd.execute(userOne, "unlock u02");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(UnlockCommand.NODE_ALREADY_UNLOCKED, context.getMessage());

        context = cmd.execute(userOne, "unlock u03");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(UnlockCommand.NODE_NOT_FOUND, context.getMessage());

        context = cmd.execute(userTwo, "unlock u01");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(UnlockCommand.NODE_LOCK_DIFF_USER, context.getMessage());

        context = cmd.execute(userOne, "unlock u01");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "unlock u01/config.u01.log");
        Assert.assertTrue(context.isCommandWasExecuted());
    }

    @Test
    public void testCommandLine_testCase07()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.execute(userOne, "mkdir u01/u034");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "rm u01/u034");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "rm u01");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "rm u01");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(RemoveCommand.NODE_NOT_FOUND, context.getMessage());

        context = cmd.execute(userOne, "print");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals
        (
            "/\n" +
            "|__applications\n" +
            "|  |__result.log\n" +
            "|__home\n" +
            "|  |__user1\n" +
            "|  |__user2\n" +
            "|__logs\n" +
            "|__u02\n" +
            "|  |__toms\n" +
            "|  |  |__u01\n" +
            "|  |  |  |__config.u01.log\n" +
            "|  |__config.u02.log\n",
            context.getMessage()
        );
    }

    @Test
    public void testCommandLine_testCase08()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.execute(userOne, "move u01 u23");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MoveDirectoryCommand.SOURCE_NOT_FOUND, context.getMessage());

        context = cmd.execute(userOne, "move applications u23");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MoveDirectoryCommand.DESTINATION_NOT_FOUND, context.getMessage());

        context = cmd.execute(userOne, "move applications u02/config.u02.log");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MoveDirectoryCommand.DESTINATION_NOT_DIRECTORY, context.getMessage());

        context = cmd.execute(userOne, "move applications u02/config.u02.log");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MoveDirectoryCommand.DESTINATION_NOT_DIRECTORY, context.getMessage());

        context = cmd.execute(userOne, "move u02/toms/u01 .");
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.execute(userOne, "print");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals
        (
            "/\n" +
            "|__applications\n" +
            "|  |__result.log\n" +
            "|__home\n" +
            "|  |__user1\n" +
            "|  |__user2\n" +
            "|__logs\n" +
            "|__u01\n" +
            "|  |__config.u01.log\n" +
            "|__u02\n" +
            "|  |__toms\n" +
            "|  |__config.u02.log\n",
            context.getMessage()
        );
    }

    @Test
    public void testCommandLine_testCase09()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.execute(userOne, "rmdir applications");
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(CommandLine.NO_SUCH_COMMAND, context.getMessage());

        context = cmd.execute(userOne, "help");
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals(HelpCommand.HELP_MESSAGE, context.getMessage());

    }
}
