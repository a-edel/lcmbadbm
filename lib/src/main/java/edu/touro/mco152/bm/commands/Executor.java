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
     * Sets the command to be executed.
     *
     * @param command The command to be executed.
     */
    public void setCommand(Command command){
        this.command = command;
    }

    /**
     * Executes the command set within this Executor.
     */
    public void executeCommand(){
        command.execute();
    }
}
