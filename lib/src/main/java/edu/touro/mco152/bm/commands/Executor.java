package edu.touro.mco152.bm.commands;

/**
 * Simple executor responsible for executing commands, in this case 'read' and 'write' commands
 */
public class Executor {

    /**
     * The command to be executed.
     */
    private Command command;

        /**
     * Executes the command set within this Executor.
     */
    public Boolean executeCommand(Command command)
    {
        return command.execute();
    }
}
