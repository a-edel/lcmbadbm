package edu.touro.mco152.bm.commands;

/**
 * Simple executor responsible for executing commands.
 */
public class Executor
{
    /**
     * Executes the given command.
     *
     * @param command The command to be executed.
     * @return true if the command executed successfully, otherwise false.
     * @throws Exception if there is an error during command execution.
     */
    public Boolean executeCommand(Command command) throws Exception
    {
        return command.execute();
    }
}
