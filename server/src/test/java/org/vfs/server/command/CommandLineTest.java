package org.vfs.server.command;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.vfs.server.command.impl.*;
import org.vfs.server.model.Context;
import org.vfs.server.model.Tree;
import org.vfs.server.user.User;
import org.vfs.server.user.UserRegistry;

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
        Tree.cleanup();
        UserRegistry.cleanup();
    }

    @Test
    public void testCommandLine_testCase01()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.toContext(null, "connect user1");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());
        userOne = context.getUser();
        Assert.assertNotNull(userOne);
        Assert.assertEquals("/home/user1", userOne.getDirectory().getFullPath());

        context = cmd.toContext(null, "connect user1");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertNull(context.getUser());
        Assert.assertEquals(ConnectCommand.USER_ALREADY_EXIST, context.getMessage());

        context = cmd.toContext(null, "connect user2");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());
        userTwo = context.getUser();
        Assert.assertNotNull(userTwo);
        Assert.assertEquals("/home/user2", userTwo.getDirectory().getFullPath());

        context = cmd.toContext(userTwo, "quit");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userTwo, "quit");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());

        context = cmd.toContext(null, "connect user2");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());
    }

    @Test
    public void testCommandLine_testCase02()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.toContext(userOne, "cd u034");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(ChangeDirectoryCommand.NODE_NOT_FOUND, context.getMessage());

        context = cmd.toContext(userOne, "cd ../..");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals("/", context.getUser().getDirectory().getFullPath());

        context = cmd.toContext(userTwo, "cd ../..");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals("/", context.getUser().getDirectory().getFullPath());

        context = cmd.toContext(userOne, "mkdir applications");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals("/", context.getUser().getDirectory().getFullPath());

        context = cmd.toContext(userOne, "mkdir applications");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MakeDirectoryCommand.DIRECTORY_NOT_CREATED, context.getMessage());

        context = cmd.toContext(userOne, "mkdir logs");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "mkdir u01");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "mkdir u02");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "mkdir u02/toms");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "print");
        cmd.execute(context);
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

        context = cmd.toContext(userOne, "mkfile applications/result.log");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "mkfile applications/result.log");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MakeFileCommand.FILE_NOT_CREATED, context.getMessage());

        context = cmd.toContext(userOne, "mkfile u01/config.u01.log");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "cd u02");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "mkfile config.u02.log");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "cd ..");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "print");
        cmd.execute(context);
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

        context= cmd.toContext(userOne, "cd applications");
        cmd.execute(context);

        context= cmd.toContext(userOne, "cd result.log");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals("/applications", context.getUser().getDirectory().getFullPath());
        Assert.assertEquals(ChangeDirectoryCommand.NODE_IS_FILE, context.getMessage());

        context = cmd.toContext(userOne, "cd ..");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "print");
        cmd.execute(context);
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

        context = cmd.toContext(userOne, "copy u03 u01");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(CopyCommand.SOURCE_NOT_FOUND, context.getMessage());

        context = cmd.toContext(userOne, "copy u01 u04");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(CopyCommand.DESTINATION_NOT_FOUND, context.getMessage());

        context = cmd.toContext(userOne, "copy applications u02/config.u02.log");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(CopyCommand.DESTINATION_NOT_DIRECTORY, context.getMessage());

        context = cmd.toContext(userOne, "copy u01 u02/toms");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "print");
        cmd.execute(context);
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

        context = cmd.toContext(userOne, "lock u01");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "lock u03");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(LockCommand.NODE_NOT_FOUND, context.getMessage());

        context = cmd.toContext(userOne, "lock u01/config.u01.log");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "rm u01/config.u01.log");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(RemoveCommand.NODES_IS_LOCKED, context.getMessage());

        context = cmd.toContext(userTwo, "lock u01/config.u01.log");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(LockCommand.NODE_NOT_LOCKED, context.getMessage());

        context = cmd.toContext(userOne, "print");
        cmd.execute(context);
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

        context = cmd.toContext(userOne, "unlock u02");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(UnlockCommand.NODE_ALREADY_UNLOCKED, context.getMessage());

        context = cmd.toContext(userOne, "unlock u03");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(UnlockCommand.NODE_NOT_FOUND, context.getMessage());

        context = cmd.toContext(userTwo, "unlock u01");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(UnlockCommand.NODE_LOCK_DIFF_USER, context.getMessage());

        context = cmd.toContext(userOne, "unlock u01");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "unlock u01/config.u01.log");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

    }

    @Test
    public void testCommandLine_testCase07()
    {
        CommandLine cmd = new CommandLine();
        Context context;

        context = cmd.toContext(userOne, "mkdir u01/u034");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "rm u01/u034");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "rm u01");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "rm u01");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(RemoveCommand.NODE_NOT_FOUND, context.getMessage());

        context = cmd.toContext(userOne, "print");
        cmd.execute(context);
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

        context = cmd.toContext(userOne, "move u01 u23");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MoveDirectoryCommand.SOURCE_NOT_FOUND, context.getMessage());

        context = cmd.toContext(userOne, "move applications u23");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MoveDirectoryCommand.DESTINATION_NOT_FOUND, context.getMessage());

        context = cmd.toContext(userOne, "move applications u02/config.u02.log");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MoveDirectoryCommand.DESTINATION_NOT_DIRECTORY, context.getMessage());

        context = cmd.toContext(userOne, "move applications u02/config.u02.log");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(MoveDirectoryCommand.DESTINATION_NOT_DIRECTORY, context.getMessage());

        context = cmd.toContext(userOne, "move u02/toms/u01 .");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());

        context = cmd.toContext(userOne, "print");
        cmd.execute(context);
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

        context = cmd.toContext(userOne, "rmdir applications");
        cmd.execute(context);
        Assert.assertFalse(context.isCommandWasExecuted());
        Assert.assertEquals(CommandLine.NO_SUCH_COMMAND, context.getMessage());

        context = cmd.toContext(userOne, "help");
        cmd.execute(context);
        Assert.assertTrue(context.isCommandWasExecuted());
        Assert.assertEquals(HelpCommand.HELP_MESSAGE, context.getMessage());

    }
}
